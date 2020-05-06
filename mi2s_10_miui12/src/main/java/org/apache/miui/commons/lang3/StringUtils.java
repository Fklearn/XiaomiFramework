package org.apache.miui.commons.lang3;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
  public static final String EMPTY = "";
  public static final int INDEX_NOT_FOUND = -1;
  private static final int PAD_LIMIT = 8192;
  private static final Pattern WHITESPACE_BLOCK = Pattern.compile("\\s+");
  
  public static String abbreviate(String paramString, int paramInt)
  {
    return abbreviate(paramString, 0, paramInt);
  }
  
  public static String abbreviate(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      return null;
    }
    if (paramInt2 >= 4)
    {
      if (paramString.length() <= paramInt2) {
        return paramString;
      }
      int i = paramInt1;
      if (paramInt1 > paramString.length()) {
        i = paramString.length();
      }
      paramInt1 = i;
      if (paramString.length() - i < paramInt2 - 3) {
        paramInt1 = paramString.length() - (paramInt2 - 3);
      }
      StringBuilder localStringBuilder;
      if (paramInt1 <= 4)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString.substring(0, paramInt2 - 3));
        localStringBuilder.append("...");
        return localStringBuilder.toString();
      }
      if (paramInt2 >= 7)
      {
        if (paramInt1 + paramInt2 - 3 < paramString.length())
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("...");
          localStringBuilder.append(abbreviate(paramString.substring(paramInt1), paramInt2 - 3));
          return localStringBuilder.toString();
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("...");
        localStringBuilder.append(paramString.substring(paramString.length() - (paramInt2 - 3)));
        return localStringBuilder.toString();
      }
      throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
    }
    throw new IllegalArgumentException("Minimum abbreviation width is 4");
  }
  
  public static String abbreviateMiddle(String paramString1, String paramString2, int paramInt)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      if ((paramInt < paramString1.length()) && (paramInt >= paramString2.length() + 2))
      {
        int i = paramInt - paramString2.length();
        int j = i / 2;
        int k = paramString1.length();
        int m = i / 2;
        StringBuilder localStringBuilder = new StringBuilder(paramInt);
        localStringBuilder.append(paramString1.substring(0, j + i % 2));
        localStringBuilder.append(paramString2);
        localStringBuilder.append(paramString1.substring(k - m));
        return localStringBuilder.toString();
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String capitalize(String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.length();
      if (i != 0)
      {
        StringBuilder localStringBuilder = new StringBuilder(i);
        localStringBuilder.append(Character.toTitleCase(paramString.charAt(0)));
        localStringBuilder.append(paramString.substring(1));
        return localStringBuilder.toString();
      }
    }
    return paramString;
  }
  
  public static String center(String paramString, int paramInt)
  {
    return center(paramString, paramInt, ' ');
  }
  
  public static String center(String paramString, int paramInt, char paramChar)
  {
    if ((paramString != null) && (paramInt > 0))
    {
      int i = paramString.length();
      int j = paramInt - i;
      if (j <= 0) {
        return paramString;
      }
      return rightPad(leftPad(paramString, j / 2 + i, paramChar), paramInt, paramChar);
    }
    return paramString;
  }
  
  public static String center(String paramString1, int paramInt, String paramString2)
  {
    if ((paramString1 != null) && (paramInt > 0))
    {
      String str = paramString2;
      if (isEmpty(paramString2)) {
        str = " ";
      }
      int i = paramString1.length();
      int j = paramInt - i;
      if (j <= 0) {
        return paramString1;
      }
      return rightPad(leftPad(paramString1, j / 2 + i, str), paramInt, str);
    }
    return paramString1;
  }
  
  public static String chomp(String paramString)
  {
    if (isEmpty(paramString)) {
      return paramString;
    }
    int i;
    if (paramString.length() == 1)
    {
      i = paramString.charAt(0);
      if ((i != 13) && (i != 10)) {
        return paramString;
      }
      return "";
    }
    int j = paramString.length() - 1;
    int k = paramString.charAt(j);
    if (k == 10)
    {
      i = j;
      if (paramString.charAt(j - 1) == '\r') {
        i = j - 1;
      }
    }
    else
    {
      i = j;
      if (k != 13) {
        i = j + 1;
      }
    }
    return paramString.substring(0, i);
  }
  
  @Deprecated
  public static String chomp(String paramString1, String paramString2)
  {
    return removeEnd(paramString1, paramString2);
  }
  
  public static String chop(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if (i < 2) {
      return "";
    }
    i--;
    String str = paramString.substring(0, i);
    if ((paramString.charAt(i) == '\n') && (str.charAt(i - 1) == '\r')) {
      return str.substring(0, i - 1);
    }
    return str;
  }
  
  public static boolean contains(CharSequence paramCharSequence, int paramInt)
  {
    boolean bool1 = isEmpty(paramCharSequence);
    boolean bool2 = false;
    if (bool1) {
      return false;
    }
    if (CharSequenceUtils.indexOf(paramCharSequence, paramInt, 0) >= 0) {
      bool2 = true;
    }
    return bool2;
  }
  
  public static boolean contains(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    boolean bool = false;
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      if (CharSequenceUtils.indexOf(paramCharSequence1, paramCharSequence2, 0) >= 0) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public static boolean containsAny(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramCharSequence2 == null) {
      return false;
    }
    return containsAny(paramCharSequence1, CharSequenceUtils.toCharArray(paramCharSequence2));
  }
  
  public static boolean containsAny(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((!isEmpty(paramCharSequence)) && (!ArrayUtils.isEmpty(paramVarArgs)))
    {
      int i = paramCharSequence.length();
      int j = paramVarArgs.length;
      for (int k = 0; k < i; k++)
      {
        char c = paramCharSequence.charAt(k);
        for (int m = 0; m < j; m++) {
          if (paramVarArgs[m] == c) {
            if (Character.isHighSurrogate(c))
            {
              if (m == j - 1) {
                return true;
              }
              if ((k < i - 1) && (paramVarArgs[(m + 1)] == paramCharSequence.charAt(k + 1))) {
                return true;
              }
            }
            else
            {
              return true;
            }
          }
        }
      }
      return false;
    }
    return false;
  }
  
  public static boolean containsIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      int i = paramCharSequence2.length();
      int j = paramCharSequence1.length();
      for (int k = 0; k <= j - i; k++) {
        if (CharSequenceUtils.regionMatches(paramCharSequence1, true, k, paramCharSequence2, 0, i)) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  public static boolean containsNone(CharSequence paramCharSequence, String paramString)
  {
    if ((paramCharSequence != null) && (paramString != null)) {
      return containsNone(paramCharSequence, paramString.toCharArray());
    }
    return true;
  }
  
  public static boolean containsNone(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((paramCharSequence != null) && (paramVarArgs != null))
    {
      int i = paramCharSequence.length();
      int j = paramVarArgs.length;
      for (int k = 0; k < i; k++)
      {
        char c = paramCharSequence.charAt(k);
        for (int m = 0; m < j; m++) {
          if (paramVarArgs[m] == c) {
            if (Character.isHighSurrogate(c))
            {
              if (m == j - 1) {
                return false;
              }
              if ((k < i - 1) && (paramVarArgs[(m + 1)] == paramCharSequence.charAt(k + 1))) {
                return false;
              }
            }
            else
            {
              return false;
            }
          }
        }
      }
      return true;
    }
    return true;
  }
  
  public static boolean containsOnly(CharSequence paramCharSequence, String paramString)
  {
    if ((paramCharSequence != null) && (paramString != null)) {
      return containsOnly(paramCharSequence, paramString.toCharArray());
    }
    return false;
  }
  
  public static boolean containsOnly(CharSequence paramCharSequence, char... paramVarArgs)
  {
    boolean bool = false;
    if ((paramVarArgs != null) && (paramCharSequence != null))
    {
      if (paramCharSequence.length() == 0) {
        return true;
      }
      if (paramVarArgs.length == 0) {
        return false;
      }
      if (indexOfAnyBut(paramCharSequence, paramVarArgs) == -1) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public static boolean containsWhitespace(CharSequence paramCharSequence)
  {
    if (isEmpty(paramCharSequence)) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if (Character.isWhitespace(paramCharSequence.charAt(j))) {
        return true;
      }
    }
    return false;
  }
  
  public static int countMatches(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((!isEmpty(paramCharSequence1)) && (!isEmpty(paramCharSequence2)))
    {
      int i = 0;
      int j = 0;
      for (;;)
      {
        j = CharSequenceUtils.indexOf(paramCharSequence1, paramCharSequence2, j);
        if (j == -1) {
          break;
        }
        i++;
        j += paramCharSequence2.length();
      }
      return i;
    }
    return 0;
  }
  
  public static <T extends CharSequence> T defaultIfBlank(T paramT1, T paramT2)
  {
    if (isBlank(paramT1)) {
      paramT1 = paramT2;
    }
    return paramT1;
  }
  
  public static <T extends CharSequence> T defaultIfEmpty(T paramT1, T paramT2)
  {
    if (isEmpty(paramT1)) {
      paramT1 = paramT2;
    }
    return paramT1;
  }
  
  public static String defaultString(String paramString)
  {
    if (paramString == null) {
      paramString = "";
    }
    return paramString;
  }
  
  public static String defaultString(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      paramString1 = paramString2;
    }
    return paramString1;
  }
  
  public static String deleteWhitespace(String paramString)
  {
    if (isEmpty(paramString)) {
      return paramString;
    }
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    int j = 0;
    int k = 0;
    while (k < i)
    {
      int m = j;
      if (!Character.isWhitespace(paramString.charAt(k)))
      {
        arrayOfChar[j] = paramString.charAt(k);
        m = j + 1;
      }
      k++;
      j = m;
    }
    if (j == i) {
      return paramString;
    }
    return new String(arrayOfChar, 0, j);
  }
  
  public static String difference(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return paramString2;
    }
    if (paramString2 == null) {
      return paramString1;
    }
    int i = indexOfDifference(paramString1, paramString2);
    if (i == -1) {
      return "";
    }
    return paramString2.substring(i);
  }
  
  public static boolean endsWith(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return endsWith(paramCharSequence1, paramCharSequence2, false);
  }
  
  private static boolean endsWith(CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    boolean bool = false;
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      if (paramCharSequence2.length() > paramCharSequence1.length()) {
        return false;
      }
      return CharSequenceUtils.regionMatches(paramCharSequence1, paramBoolean, paramCharSequence1.length() - paramCharSequence2.length(), paramCharSequence2, 0, paramCharSequence2.length());
    }
    paramBoolean = bool;
    if (paramCharSequence1 == null)
    {
      paramBoolean = bool;
      if (paramCharSequence2 == null) {
        paramBoolean = true;
      }
    }
    return paramBoolean;
  }
  
  public static boolean endsWithAny(CharSequence paramCharSequence, CharSequence... paramVarArgs)
  {
    if ((!isEmpty(paramCharSequence)) && (!ArrayUtils.isEmpty(paramVarArgs)))
    {
      int i = paramVarArgs.length;
      for (int j = 0; j < i; j++) {
        if (endsWith(paramCharSequence, paramVarArgs[j])) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  public static boolean endsWithIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return endsWith(paramCharSequence1, paramCharSequence2, true);
  }
  
  public static boolean equals(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    boolean bool;
    if (paramCharSequence1 == null)
    {
      if (paramCharSequence2 == null) {
        bool = true;
      } else {
        bool = false;
      }
    }
    else {
      bool = paramCharSequence1.equals(paramCharSequence2);
    }
    return bool;
  }
  
  public static boolean equalsIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return CharSequenceUtils.regionMatches(paramCharSequence1, true, 0, paramCharSequence2, 0, Math.max(paramCharSequence1.length(), paramCharSequence2.length()));
    }
    boolean bool;
    if (paramCharSequence1 == paramCharSequence2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static String getCommonPrefix(String... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length != 0))
    {
      int i = indexOfDifference(paramVarArgs);
      if (i == -1)
      {
        if (paramVarArgs[0] == null) {
          return "";
        }
        return paramVarArgs[0];
      }
      if (i == 0) {
        return "";
      }
      return paramVarArgs[0].substring(0, i);
    }
    return "";
  }
  
  public static int getLevenshteinDistance(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      int i = paramCharSequence1.length();
      int j = paramCharSequence2.length();
      if (i == 0) {
        return j;
      }
      if (j == 0) {
        return i;
      }
      int k = i;
      int m = j;
      CharSequence localCharSequence1 = paramCharSequence1;
      CharSequence localCharSequence2 = paramCharSequence2;
      if (i > j)
      {
        k = j;
        m = paramCharSequence1.length();
        localCharSequence2 = paramCharSequence1;
        localCharSequence1 = paramCharSequence2;
      }
      paramCharSequence2 = new int[k + 1];
      paramCharSequence1 = new int[k + 1];
      for (j = 0; j <= k; j++) {
        paramCharSequence2[j] = j;
      }
      j = 1;
      while (j <= m)
      {
        int n = localCharSequence2.charAt(j - 1);
        paramCharSequence1[0] = j;
        for (i = 1; i <= k; i++)
        {
          int i1;
          if (localCharSequence1.charAt(i - 1) == n) {
            i1 = 0;
          } else {
            i1 = 1;
          }
          paramCharSequence1[i] = Math.min(Math.min(paramCharSequence1[(i - 1)] + 1, paramCharSequence2[i] + 1), paramCharSequence2[(i - 1)] + i1);
        }
        CharSequence localCharSequence3 = paramCharSequence1;
        paramCharSequence1 = paramCharSequence2;
        j++;
        paramCharSequence2 = localCharSequence3;
      }
      return paramCharSequence2[k];
    }
    throw new IllegalArgumentException("Strings must not be null");
  }
  
  public static int getLevenshteinDistance(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      if (paramInt >= 0)
      {
        int i = paramCharSequence1.length();
        int j = paramCharSequence2.length();
        int k = -1;
        if (i == 0)
        {
          if (j <= paramInt) {
            k = j;
          }
          return k;
        }
        if (j == 0)
        {
          if (i <= paramInt) {
            k = i;
          }
          return k;
        }
        CharSequence localCharSequence1;
        CharSequence localCharSequence2;
        if (i > j)
        {
          k = paramCharSequence1.length();
          i = j;
          j = k;
          localCharSequence1 = paramCharSequence2;
          localCharSequence2 = paramCharSequence1;
        }
        else
        {
          localCharSequence2 = paramCharSequence2;
          localCharSequence1 = paramCharSequence1;
        }
        paramCharSequence2 = new int[i + 1];
        paramCharSequence1 = new int[i + 1];
        int m = Math.min(i, paramInt) + 1;
        for (k = 0; k < m; k++) {
          paramCharSequence2[k] = k;
        }
        Arrays.fill(paramCharSequence2, m, paramCharSequence2.length, Integer.MAX_VALUE);
        Arrays.fill(paramCharSequence1, Integer.MAX_VALUE);
        k = 1;
        while (k <= j)
        {
          int n = localCharSequence2.charAt(k - 1);
          paramCharSequence1[0] = k;
          m = Math.max(1, k - paramInt);
          int i1 = Math.min(i, k + paramInt);
          if (m > i1) {
            return -1;
          }
          if (m > 1) {
            paramCharSequence1[(m - 1)] = Integer.MAX_VALUE;
          }
          while (m <= i1)
          {
            if (localCharSequence1.charAt(m - 1) == n) {
              paramCharSequence1[m] = paramCharSequence2[(m - 1)];
            } else {
              paramCharSequence1[m] = (Math.min(Math.min(paramCharSequence1[(m - 1)], paramCharSequence2[m]), paramCharSequence2[(m - 1)]) + 1);
            }
            m++;
          }
          CharSequence localCharSequence3 = paramCharSequence2;
          k++;
          paramCharSequence2 = paramCharSequence1;
          paramCharSequence1 = localCharSequence3;
        }
        if (paramCharSequence2[i] <= paramInt) {
          return paramCharSequence2[i];
        }
        return -1;
      }
      throw new IllegalArgumentException("Threshold must not be negative");
    }
    throw new IllegalArgumentException("Strings must not be null");
  }
  
  public static int indexOf(CharSequence paramCharSequence, int paramInt)
  {
    if (isEmpty(paramCharSequence)) {
      return -1;
    }
    return CharSequenceUtils.indexOf(paramCharSequence, paramInt, 0);
  }
  
  public static int indexOf(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    if (isEmpty(paramCharSequence)) {
      return -1;
    }
    return CharSequenceUtils.indexOf(paramCharSequence, paramInt1, paramInt2);
  }
  
  public static int indexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return CharSequenceUtils.indexOf(paramCharSequence1, paramCharSequence2, 0);
    }
    return -1;
  }
  
  public static int indexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return CharSequenceUtils.indexOf(paramCharSequence1, paramCharSequence2, paramInt);
    }
    return -1;
  }
  
  public static int indexOfAny(CharSequence paramCharSequence, String paramString)
  {
    if ((!isEmpty(paramCharSequence)) && (!isEmpty(paramString))) {
      return indexOfAny(paramCharSequence, paramString.toCharArray());
    }
    return -1;
  }
  
  public static int indexOfAny(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((!isEmpty(paramCharSequence)) && (!ArrayUtils.isEmpty(paramVarArgs)))
    {
      int i = paramCharSequence.length();
      int j = paramVarArgs.length;
      for (int k = 0; k < i; k++)
      {
        char c = paramCharSequence.charAt(k);
        for (int m = 0; m < j; m++) {
          if (paramVarArgs[m] == c) {
            if ((k < i - 1) && (m < j - 1) && (Character.isHighSurrogate(c)))
            {
              if (paramVarArgs[(m + 1)] == paramCharSequence.charAt(k + 1)) {
                return k;
              }
            }
            else {
              return k;
            }
          }
        }
      }
      return -1;
    }
    return -1;
  }
  
  public static int indexOfAny(CharSequence paramCharSequence, CharSequence... paramVarArgs)
  {
    int i = -1;
    if ((paramCharSequence != null) && (paramVarArgs != null))
    {
      int j = paramVarArgs.length;
      int k = Integer.MAX_VALUE;
      int m = 0;
      while (m < j)
      {
        CharSequence localCharSequence = paramVarArgs[m];
        int n;
        if (localCharSequence == null)
        {
          n = k;
        }
        else
        {
          int i1 = CharSequenceUtils.indexOf(paramCharSequence, localCharSequence, 0);
          if (i1 == -1)
          {
            n = k;
          }
          else
          {
            n = k;
            if (i1 < k) {
              n = i1;
            }
          }
        }
        m++;
        k = n;
      }
      if (k == Integer.MAX_VALUE) {
        k = i;
      }
      return k;
    }
    return -1;
  }
  
  public static int indexOfAnyBut(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((!isEmpty(paramCharSequence1)) && (!isEmpty(paramCharSequence2)))
    {
      int i = paramCharSequence1.length();
      for (int j = 0; j < i; j++)
      {
        char c = paramCharSequence1.charAt(j);
        int k;
        if (CharSequenceUtils.indexOf(paramCharSequence2, c, 0) >= 0) {
          k = 1;
        } else {
          k = 0;
        }
        if ((j + 1 < i) && (Character.isHighSurrogate(c)))
        {
          int m = paramCharSequence1.charAt(j + 1);
          if ((k != 0) && (CharSequenceUtils.indexOf(paramCharSequence2, m, 0) < 0)) {
            return j;
          }
        }
        else if (k == 0)
        {
          return j;
        }
      }
      return -1;
    }
    return -1;
  }
  
  public static int indexOfAnyBut(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((!isEmpty(paramCharSequence)) && (!ArrayUtils.isEmpty(paramVarArgs)))
    {
      int i = paramCharSequence.length();
      int j = paramVarArgs.length;
      int k = 0;
      if (k < i)
      {
        char c = paramCharSequence.charAt(k);
        for (int m = 0;; m++)
        {
          if (m >= j) {
            break label119;
          }
          if ((paramVarArgs[m] == c) && ((k >= i - 1) || (m >= j - 1) || (!Character.isHighSurrogate(c)) || (paramVarArgs[(m + 1)] == paramCharSequence.charAt(k + 1))))
          {
            k++;
            break;
          }
        }
        label119:
        return k;
      }
      return -1;
    }
    return -1;
  }
  
  public static int indexOfDifference(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramCharSequence1 == paramCharSequence2) {
      return -1;
    }
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      for (int i = 0; (i < paramCharSequence1.length()) && (i < paramCharSequence2.length()) && (paramCharSequence1.charAt(i) == paramCharSequence2.charAt(i)); i++) {}
      if ((i >= paramCharSequence2.length()) && (i >= paramCharSequence1.length())) {
        return -1;
      }
      return i;
    }
    return 0;
  }
  
  public static int indexOfDifference(CharSequence... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length > 1))
    {
      int i = 0;
      int j = 1;
      int k = paramVarArgs.length;
      int m = Integer.MAX_VALUE;
      int n = 0;
      int i1 = 0;
      int i2;
      while (i1 < k)
      {
        if (paramVarArgs[i1] == null)
        {
          i = 1;
          i2 = 0;
        }
        else
        {
          j = 0;
          i2 = Math.min(paramVarArgs[i1].length(), m);
          n = Math.max(paramVarArgs[i1].length(), n);
        }
        i1++;
        m = i2;
      }
      if ((j == 0) && ((n != 0) || (i != 0)))
      {
        if (m == 0) {
          return 0;
        }
        i1 = -1;
        i2 = 0;
        for (;;)
        {
          j = i1;
          if (i2 >= m) {
            break;
          }
          int i3 = paramVarArgs[0].charAt(i2);
          for (i = 1;; i++)
          {
            j = i1;
            if (i >= k) {
              break;
            }
            if (paramVarArgs[i].charAt(i2) != i3)
            {
              j = i2;
              break;
            }
          }
          if (j != -1) {
            break;
          }
          i2++;
          i1 = j;
        }
        if ((j == -1) && (m != n)) {
          return m;
        }
        return j;
      }
      return -1;
    }
    return -1;
  }
  
  public static int indexOfIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return indexOfIgnoreCase(paramCharSequence1, paramCharSequence2, 0);
  }
  
  public static int indexOfIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      int i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      int j = paramCharSequence1.length() - paramCharSequence2.length() + 1;
      if (i > j) {
        return -1;
      }
      if (paramCharSequence2.length() == 0) {
        return i;
      }
      for (paramInt = i; paramInt < j; paramInt++) {
        if (CharSequenceUtils.regionMatches(paramCharSequence1, true, paramInt, paramCharSequence2, 0, paramCharSequence2.length())) {
          return paramInt;
        }
      }
      return -1;
    }
    return -1;
  }
  
  public static boolean isAllLowerCase(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (!isEmpty(paramCharSequence)))
    {
      int i = paramCharSequence.length();
      for (int j = 0; j < i; j++) {
        if (!Character.isLowerCase(paramCharSequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static boolean isAllUpperCase(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (!isEmpty(paramCharSequence)))
    {
      int i = paramCharSequence.length();
      for (int j = 0; j < i; j++) {
        if (!Character.isUpperCase(paramCharSequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static boolean isAlpha(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (paramCharSequence.length() != 0))
    {
      int i = paramCharSequence.length();
      for (int j = 0; j < i; j++) {
        if (!Character.isLetter(paramCharSequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static boolean isAlphaSpace(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if ((!Character.isLetter(paramCharSequence.charAt(j))) && (paramCharSequence.charAt(j) != ' ')) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isAlphanumeric(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (paramCharSequence.length() != 0))
    {
      int i = paramCharSequence.length();
      for (int j = 0; j < i; j++) {
        if (!Character.isLetterOrDigit(paramCharSequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static boolean isAlphanumericSpace(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if ((!Character.isLetterOrDigit(paramCharSequence.charAt(j))) && (paramCharSequence.charAt(j) != ' ')) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isAsciiPrintable(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if (!CharUtils.isAsciiPrintable(paramCharSequence.charAt(j))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isBlank(CharSequence paramCharSequence)
  {
    if (paramCharSequence != null)
    {
      int i = paramCharSequence.length();
      if (i != 0)
      {
        for (int j = 0; j < i; j++) {
          if (!Character.isWhitespace(paramCharSequence.charAt(j))) {
            return false;
          }
        }
        return true;
      }
    }
    return true;
  }
  
  public static boolean isEmpty(CharSequence paramCharSequence)
  {
    boolean bool;
    if ((paramCharSequence != null) && (paramCharSequence.length() != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isNotBlank(CharSequence paramCharSequence)
  {
    return isBlank(paramCharSequence) ^ true;
  }
  
  public static boolean isNotEmpty(CharSequence paramCharSequence)
  {
    return isEmpty(paramCharSequence) ^ true;
  }
  
  public static boolean isNumeric(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (paramCharSequence.length() != 0))
    {
      int i = paramCharSequence.length();
      for (int j = 0; j < i; j++) {
        if (!Character.isDigit(paramCharSequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static boolean isNumericSpace(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if ((!Character.isDigit(paramCharSequence.charAt(j))) && (paramCharSequence.charAt(j) != ' ')) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isWhitespace(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if (!Character.isWhitespace(paramCharSequence.charAt(j))) {
        return false;
      }
    }
    return true;
  }
  
  public static String join(Iterable<?> paramIterable, char paramChar)
  {
    if (paramIterable == null) {
      return null;
    }
    return join(paramIterable.iterator(), paramChar);
  }
  
  public static String join(Iterable<?> paramIterable, String paramString)
  {
    if (paramIterable == null) {
      return null;
    }
    return join(paramIterable.iterator(), paramString);
  }
  
  public static String join(Iterator<?> paramIterator, char paramChar)
  {
    if (paramIterator == null) {
      return null;
    }
    if (!paramIterator.hasNext()) {
      return "";
    }
    Object localObject = paramIterator.next();
    if (!paramIterator.hasNext()) {
      return ObjectUtils.toString(localObject);
    }
    StringBuilder localStringBuilder = new StringBuilder(256);
    if (localObject != null) {
      localStringBuilder.append(localObject);
    }
    while (paramIterator.hasNext())
    {
      localStringBuilder.append(paramChar);
      localObject = paramIterator.next();
      if (localObject != null) {
        localStringBuilder.append(localObject);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String join(Iterator<?> paramIterator, String paramString)
  {
    if (paramIterator == null) {
      return null;
    }
    if (!paramIterator.hasNext()) {
      return "";
    }
    Object localObject = paramIterator.next();
    if (!paramIterator.hasNext()) {
      return ObjectUtils.toString(localObject);
    }
    StringBuilder localStringBuilder = new StringBuilder(256);
    if (localObject != null) {
      localStringBuilder.append(localObject);
    }
    while (paramIterator.hasNext())
    {
      if (paramString != null) {
        localStringBuilder.append(paramString);
      }
      localObject = paramIterator.next();
      if (localObject != null) {
        localStringBuilder.append(localObject);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static <T> String join(T... paramVarArgs)
  {
    return join(paramVarArgs, null);
  }
  
  public static String join(Object[] paramArrayOfObject, char paramChar)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    return join(paramArrayOfObject, paramChar, 0, paramArrayOfObject.length);
  }
  
  public static String join(Object[] paramArrayOfObject, char paramChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    int i = paramInt2 - paramInt1;
    if (i <= 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(i * 16);
    for (i = paramInt1; i < paramInt2; i++)
    {
      if (i > paramInt1) {
        localStringBuilder.append(paramChar);
      }
      if (paramArrayOfObject[i] != null) {
        localStringBuilder.append(paramArrayOfObject[i]);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String join(Object[] paramArrayOfObject, String paramString)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    return join(paramArrayOfObject, paramString, 0, paramArrayOfObject.length);
  }
  
  public static String join(Object[] paramArrayOfObject, String paramString, int paramInt1, int paramInt2)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    int i = paramInt2 - paramInt1;
    if (i <= 0) {
      return "";
    }
    paramString = new StringBuilder(i * 16);
    for (i = paramInt1; i < paramInt2; i++)
    {
      if (i > paramInt1) {
        paramString.append(str);
      }
      if (paramArrayOfObject[i] != null) {
        paramString.append(paramArrayOfObject[i]);
      }
    }
    return paramString.toString();
  }
  
  public static int lastIndexOf(CharSequence paramCharSequence, int paramInt)
  {
    if (isEmpty(paramCharSequence)) {
      return -1;
    }
    return CharSequenceUtils.lastIndexOf(paramCharSequence, paramInt, paramCharSequence.length());
  }
  
  public static int lastIndexOf(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    if (isEmpty(paramCharSequence)) {
      return -1;
    }
    return CharSequenceUtils.lastIndexOf(paramCharSequence, paramInt1, paramInt2);
  }
  
  public static int lastIndexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return CharSequenceUtils.lastIndexOf(paramCharSequence1, paramCharSequence2, paramCharSequence1.length());
    }
    return -1;
  }
  
  public static int lastIndexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return CharSequenceUtils.lastIndexOf(paramCharSequence1, paramCharSequence2, paramInt);
    }
    return -1;
  }
  
  public static int lastIndexOfAny(CharSequence paramCharSequence, CharSequence... paramVarArgs)
  {
    if ((paramCharSequence != null) && (paramVarArgs != null))
    {
      int i = paramVarArgs.length;
      int j = -1;
      int k = 0;
      while (k < i)
      {
        CharSequence localCharSequence = paramVarArgs[k];
        int m;
        if (localCharSequence == null)
        {
          m = j;
        }
        else
        {
          int n = CharSequenceUtils.lastIndexOf(paramCharSequence, localCharSequence, paramCharSequence.length());
          m = j;
          if (n > j) {
            m = n;
          }
        }
        k++;
        j = m;
      }
      return j;
    }
    return -1;
  }
  
  public static int lastIndexOfIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
      return lastIndexOfIgnoreCase(paramCharSequence1, paramCharSequence2, paramCharSequence1.length());
    }
    return -1;
  }
  
  public static int lastIndexOfIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      int i = paramInt;
      if (paramInt > paramCharSequence1.length() - paramCharSequence2.length()) {
        i = paramCharSequence1.length() - paramCharSequence2.length();
      }
      if (i < 0) {
        return -1;
      }
      if (paramCharSequence2.length() == 0) {
        return i;
      }
      while (i >= 0)
      {
        if (CharSequenceUtils.regionMatches(paramCharSequence1, true, i, paramCharSequence2, 0, paramCharSequence2.length())) {
          return i;
        }
        i--;
      }
      return -1;
    }
    return -1;
  }
  
  public static int lastOrdinalIndexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    return ordinalIndexOf(paramCharSequence1, paramCharSequence2, paramInt, true);
  }
  
  public static String left(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    if (paramInt < 0) {
      return "";
    }
    if (paramString.length() <= paramInt) {
      return paramString;
    }
    return paramString.substring(0, paramInt);
  }
  
  public static String leftPad(String paramString, int paramInt)
  {
    return leftPad(paramString, paramInt, ' ');
  }
  
  public static String leftPad(String paramString, int paramInt, char paramChar)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramInt - paramString.length();
    if (i <= 0) {
      return paramString;
    }
    if (i > 8192) {
      return leftPad(paramString, paramInt, String.valueOf(paramChar));
    }
    return repeat(paramChar, i).concat(paramString);
  }
  
  public static String leftPad(String paramString1, int paramInt, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    Object localObject = paramString2;
    if (isEmpty(paramString2)) {
      localObject = " ";
    }
    int i = ((String)localObject).length();
    int j = paramInt - paramString1.length();
    if (j <= 0) {
      return paramString1;
    }
    if ((i == 1) && (j <= 8192)) {
      return leftPad(paramString1, paramInt, ((String)localObject).charAt(0));
    }
    if (j == i) {
      return ((String)localObject).concat(paramString1);
    }
    if (j < i) {
      return ((String)localObject).substring(0, j).concat(paramString1);
    }
    paramString2 = new char[j];
    localObject = ((String)localObject).toCharArray();
    for (paramInt = 0; paramInt < j; paramInt++) {
      paramString2[paramInt] = ((char)localObject[(paramInt % i)]);
    }
    return new String(paramString2).concat(paramString1);
  }
  
  public static int length(CharSequence paramCharSequence)
  {
    int i;
    if (paramCharSequence == null) {
      i = 0;
    } else {
      i = paramCharSequence.length();
    }
    return i;
  }
  
  public static String lowerCase(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.toLowerCase();
  }
  
  public static String lowerCase(String paramString, Locale paramLocale)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.toLowerCase(paramLocale);
  }
  
  public static String mid(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      return null;
    }
    if ((paramInt2 >= 0) && (paramInt1 <= paramString.length()))
    {
      int i = paramInt1;
      if (paramInt1 < 0) {
        i = 0;
      }
      if (paramString.length() <= i + paramInt2) {
        return paramString.substring(i);
      }
      return paramString.substring(i, i + paramInt2);
    }
    return "";
  }
  
  public static String normalizeSpace(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return WHITESPACE_BLOCK.matcher(trim(paramString)).replaceAll(" ");
  }
  
  public static int ordinalIndexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    return ordinalIndexOf(paramCharSequence1, paramCharSequence2, paramInt, false);
  }
  
  private static int ordinalIndexOf(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt, boolean paramBoolean)
  {
    int i = -1;
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null) && (paramInt > 0))
    {
      if (paramCharSequence2.length() == 0)
      {
        if (paramBoolean) {
          paramInt = paramCharSequence1.length();
        } else {
          paramInt = 0;
        }
        return paramInt;
      }
      int j = 0;
      int k = j;
      if (paramBoolean)
      {
        i = paramCharSequence1.length();
        k = j;
      }
      int m;
      do
      {
        if (paramBoolean) {
          j = CharSequenceUtils.lastIndexOf(paramCharSequence1, paramCharSequence2, i - 1);
        } else {
          j = CharSequenceUtils.indexOf(paramCharSequence1, paramCharSequence2, i + 1);
        }
        if (j < 0) {
          return j;
        }
        m = k + 1;
        i = j;
        k = m;
      } while (m < paramInt);
      return j;
    }
    return -1;
  }
  
  public static String overlay(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    if (paramString1 == null) {
      return null;
    }
    String str = paramString2;
    if (paramString2 == null) {
      str = "";
    }
    int i = paramString1.length();
    int j = paramInt1;
    if (paramInt1 < 0) {
      j = 0;
    }
    paramInt1 = j;
    if (j > i) {
      paramInt1 = i;
    }
    j = paramInt2;
    if (paramInt2 < 0) {
      j = 0;
    }
    paramInt2 = j;
    if (j > i) {
      paramInt2 = i;
    }
    int k = paramInt1;
    j = paramInt2;
    if (paramInt1 > paramInt2)
    {
      j = paramInt1;
      k = paramInt2;
    }
    paramString2 = new StringBuilder(i + k - j + str.length() + 1);
    paramString2.append(paramString1.substring(0, k));
    paramString2.append(str);
    paramString2.append(paramString1.substring(j));
    return paramString2.toString();
  }
  
  public static String remove(String paramString, char paramChar)
  {
    if ((!isEmpty(paramString)) && (paramString.indexOf(paramChar) != -1))
    {
      paramString = paramString.toCharArray();
      int i = 0;
      int j = 0;
      while (j < paramString.length)
      {
        int k = i;
        if (paramString[j] != paramChar)
        {
          paramString[i] = ((char)paramString[j]);
          k = i + 1;
        }
        j++;
        i = k;
      }
      return new String(paramString, 0, i);
    }
    return paramString;
  }
  
  public static String remove(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2))) {
      return replace(paramString1, paramString2, "", -1);
    }
    return paramString1;
  }
  
  private static String removeAccentsJava6(CharSequence paramCharSequence)
    throws IllegalAccessException, InvocationTargetException
  {
    if ((InitStripAccents.java6NormalizeMethod != null) && (InitStripAccents.java6NormalizerFormNFD != null))
    {
      paramCharSequence = (String)InitStripAccents.java6NormalizeMethod.invoke(null, new Object[] { paramCharSequence, InitStripAccents.java6NormalizerFormNFD });
      return InitStripAccents.java6Pattern.matcher(paramCharSequence).replaceAll("");
    }
    throw new IllegalStateException("java.text.Normalizer is not available", InitStripAccents.java6Exception);
  }
  
  private static String removeAccentsSUN(CharSequence paramCharSequence)
    throws IllegalAccessException, InvocationTargetException
  {
    if (InitStripAccents.sunDecomposeMethod != null)
    {
      paramCharSequence = (String)InitStripAccents.sunDecomposeMethod.invoke(null, new Object[] { paramCharSequence, Boolean.FALSE, Integer.valueOf(0) });
      return InitStripAccents.sunPattern.matcher(paramCharSequence).replaceAll("");
    }
    throw new IllegalStateException("sun.text.Normalizer is not available", InitStripAccents.sunException);
  }
  
  public static String removeEnd(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      if (paramString1.endsWith(paramString2)) {
        return paramString1.substring(0, paramString1.length() - paramString2.length());
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String removeEndIgnoreCase(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      if (endsWithIgnoreCase(paramString1, paramString2)) {
        return paramString1.substring(0, paramString1.length() - paramString2.length());
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String removeStart(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      if (paramString1.startsWith(paramString2)) {
        return paramString1.substring(paramString2.length());
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String removeStartIgnoreCase(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      if (startsWithIgnoreCase(paramString1, paramString2)) {
        return paramString1.substring(paramString2.length());
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String repeat(char paramChar, int paramInt)
  {
    char[] arrayOfChar = new char[paramInt];
    paramInt--;
    while (paramInt >= 0)
    {
      arrayOfChar[paramInt] = ((char)paramChar);
      paramInt--;
    }
    return new String(arrayOfChar);
  }
  
  public static String repeat(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    if (paramInt <= 0) {
      return "";
    }
    int i = paramString.length();
    if ((paramInt != 1) && (i != 0))
    {
      if ((i == 1) && (paramInt <= 8192)) {
        return repeat(paramString.charAt(0), paramInt);
      }
      int j = i * paramInt;
      if (i != 1)
      {
        if (i != 2)
        {
          StringBuilder localStringBuilder = new StringBuilder(j);
          for (i = 0; i < paramInt; i++) {
            localStringBuilder.append(paramString);
          }
          return localStringBuilder.toString();
        }
        i = paramString.charAt(0);
        int k = paramString.charAt(1);
        paramString = new char[j];
        for (paramInt = paramInt * 2 - 2; paramInt >= 0; paramInt = paramInt - 1 - 1)
        {
          paramString[paramInt] = ((char)i);
          paramString[(paramInt + 1)] = ((char)k);
        }
        return new String(paramString);
      }
      return repeat(paramString.charAt(0), paramInt);
    }
    return paramString;
  }
  
  public static String repeat(String paramString1, String paramString2, int paramInt)
  {
    if ((paramString1 != null) && (paramString2 != null))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString1);
      localStringBuilder.append(paramString2);
      return removeEnd(repeat(localStringBuilder.toString(), paramInt), paramString2);
    }
    return repeat(paramString1, paramInt);
  }
  
  public static String replace(String paramString1, String paramString2, String paramString3)
  {
    return replace(paramString1, paramString2, paramString3, -1);
  }
  
  public static String replace(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)) && (paramString3 != null) && (paramInt != 0))
    {
      int i = 0;
      int j = paramString1.indexOf(paramString2, 0);
      if (j == -1) {
        return paramString1;
      }
      int k = paramString2.length();
      int m = paramString3.length() - k;
      if (m < 0) {
        m = 0;
      }
      int n = 64;
      if (paramInt < 0) {
        n = 16;
      } else if (paramInt <= 64) {
        n = paramInt;
      }
      StringBuilder localStringBuilder = new StringBuilder(paramString1.length() + m * n);
      n = paramInt;
      m = j;
      paramInt = i;
      for (;;)
      {
        i = paramInt;
        if (m == -1) {
          break;
        }
        localStringBuilder.append(paramString1.substring(paramInt, m));
        localStringBuilder.append(paramString3);
        paramInt = m + k;
        n--;
        if (n == 0)
        {
          i = paramInt;
          break;
        }
        m = paramString1.indexOf(paramString2, paramInt);
      }
      localStringBuilder.append(paramString1.substring(i));
      return localStringBuilder.toString();
    }
    return paramString1;
  }
  
  public static String replaceChars(String paramString, char paramChar1, char paramChar2)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.replace(paramChar1, paramChar2);
  }
  
  public static String replaceChars(String paramString1, String paramString2, String paramString3)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      String str = paramString3;
      if (paramString3 == null) {
        str = "";
      }
      int i = 0;
      int j = str.length();
      int k = paramString1.length();
      paramString3 = new StringBuilder(k);
      for (int m = 0; m < k; m++)
      {
        char c = paramString1.charAt(m);
        int n = paramString2.indexOf(c);
        if (n >= 0)
        {
          int i1 = 1;
          i = i1;
          if (n < j)
          {
            paramString3.append(str.charAt(n));
            i = i1;
          }
        }
        else
        {
          paramString3.append(c);
        }
      }
      if (i != 0) {
        return paramString3.toString();
      }
      return paramString1;
    }
    return paramString1;
  }
  
  public static String replaceEach(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    return replaceEach(paramString, paramArrayOfString1, paramArrayOfString2, false, 0);
  }
  
  private static String replaceEach(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, int paramInt)
  {
    if ((paramString != null) && (paramString.length() != 0) && (paramArrayOfString1 != null) && (paramArrayOfString1.length != 0) && (paramArrayOfString2 != null) && (paramArrayOfString2.length != 0))
    {
      if (paramInt >= 0)
      {
        int i = paramArrayOfString1.length;
        int j = paramArrayOfString2.length;
        if (i == j)
        {
          boolean[] arrayOfBoolean = new boolean[i];
          int k = -1;
          int m = -1;
          j = 0;
          int n;
          while (j < i)
          {
            n = k;
            i1 = m;
            if (arrayOfBoolean[j] == 0)
            {
              n = k;
              i1 = m;
              if (paramArrayOfString1[j] != null)
              {
                n = k;
                i1 = m;
                if (paramArrayOfString1[j].length() != 0) {
                  if (paramArrayOfString2[j] == null)
                  {
                    n = k;
                    i1 = m;
                  }
                  else
                  {
                    i2 = paramString.indexOf(paramArrayOfString1[j]);
                    if (i2 == -1)
                    {
                      arrayOfBoolean[j] = true;
                      n = k;
                      i1 = m;
                    }
                    else if (k != -1)
                    {
                      n = k;
                      i1 = m;
                      if (i2 >= k) {}
                    }
                    else
                    {
                      n = i2;
                      i1 = j;
                    }
                  }
                }
              }
            }
            j++;
            k = n;
            m = i1;
          }
          if (k == -1) {
            return paramString;
          }
          int i2 = 0;
          int i1 = 0;
          j = 0;
          while (j < paramArrayOfString1.length)
          {
            n = i1;
            if (paramArrayOfString1[j] != null) {
              if (paramArrayOfString2[j] == null)
              {
                n = i1;
              }
              else
              {
                int i3 = paramArrayOfString2[j].length() - paramArrayOfString1[j].length();
                n = i1;
                if (i3 > 0) {
                  n = i1 + i3 * 3;
                }
              }
            }
            j++;
            i1 = n;
          }
          j = Math.min(i1, paramString.length() / 5);
          StringBuilder localStringBuilder = new StringBuilder(paramString.length() + j);
          j = i2;
          i1 = k;
          while (i1 != -1)
          {
            while (j < i1)
            {
              localStringBuilder.append(paramString.charAt(j));
              j++;
            }
            localStringBuilder.append(paramArrayOfString2[m]);
            n = i1 + paramArrayOfString1[m].length();
            m = -1;
            k = -1;
            j = 0;
            while (j < i)
            {
              if ((arrayOfBoolean[j] == 0) && (paramArrayOfString1[j] != null))
              {
                if (paramArrayOfString1[j].length() != 0)
                {
                  if (paramArrayOfString2[j] == null)
                  {
                    i1 = m;
                  }
                  else
                  {
                    i2 = paramString.indexOf(paramArrayOfString1[j], n);
                    if (i2 == -1)
                    {
                      arrayOfBoolean[j] = true;
                      i1 = m;
                    }
                    else if (m != -1)
                    {
                      i1 = m;
                      if (i2 >= m) {}
                    }
                    else
                    {
                      i1 = i2;
                      k = j;
                    }
                  }
                }
                else {
                  i1 = m;
                }
              }
              else {
                i1 = m;
              }
              j++;
              m = i1;
            }
            i1 = m;
            m = k;
            j = n;
          }
          k = paramString.length();
          while (j < k)
          {
            localStringBuilder.append(paramString.charAt(j));
            j++;
          }
          paramString = localStringBuilder.toString();
          if (!paramBoolean) {
            return paramString;
          }
          return replaceEach(paramString, paramArrayOfString1, paramArrayOfString2, paramBoolean, paramInt - 1);
        }
        paramString = new StringBuilder();
        paramString.append("Search and Replace array lengths don't match: ");
        paramString.append(i);
        paramString.append(" vs ");
        paramString.append(j);
        throw new IllegalArgumentException(paramString.toString());
      }
      throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
    }
    return paramString;
  }
  
  public static String replaceEachRepeatedly(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    int i;
    if (paramArrayOfString1 == null) {
      i = 0;
    } else {
      i = paramArrayOfString1.length;
    }
    return replaceEach(paramString, paramArrayOfString1, paramArrayOfString2, true, i);
  }
  
  public static String replaceOnce(String paramString1, String paramString2, String paramString3)
  {
    return replace(paramString1, paramString2, paramString3, 1);
  }
  
  public static String reverse(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new StringBuilder(paramString).reverse().toString();
  }
  
  public static String reverseDelimited(String paramString, char paramChar)
  {
    if (paramString == null) {
      return null;
    }
    paramString = split(paramString, paramChar);
    ArrayUtils.reverse(paramString);
    return join(paramString, paramChar);
  }
  
  public static String right(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    if (paramInt < 0) {
      return "";
    }
    if (paramString.length() <= paramInt) {
      return paramString;
    }
    return paramString.substring(paramString.length() - paramInt);
  }
  
  public static String rightPad(String paramString, int paramInt)
  {
    return rightPad(paramString, paramInt, ' ');
  }
  
  public static String rightPad(String paramString, int paramInt, char paramChar)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramInt - paramString.length();
    if (i <= 0) {
      return paramString;
    }
    if (i > 8192) {
      return rightPad(paramString, paramInt, String.valueOf(paramChar));
    }
    return paramString.concat(repeat(paramChar, i));
  }
  
  public static String rightPad(String paramString1, int paramInt, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    Object localObject = paramString2;
    if (isEmpty(paramString2)) {
      localObject = " ";
    }
    int i = ((String)localObject).length();
    int j = paramInt - paramString1.length();
    if (j <= 0) {
      return paramString1;
    }
    if ((i == 1) && (j <= 8192)) {
      return rightPad(paramString1, paramInt, ((String)localObject).charAt(0));
    }
    if (j == i) {
      return paramString1.concat((String)localObject);
    }
    if (j < i) {
      return paramString1.concat(((String)localObject).substring(0, j));
    }
    paramString2 = new char[j];
    localObject = ((String)localObject).toCharArray();
    for (paramInt = 0; paramInt < j; paramInt++) {
      paramString2[paramInt] = ((char)localObject[(paramInt % i)]);
    }
    return paramString1.concat(new String(paramString2));
  }
  
  public static String[] split(String paramString)
  {
    return split(paramString, null, -1);
  }
  
  public static String[] split(String paramString, char paramChar)
  {
    return splitWorker(paramString, paramChar, false);
  }
  
  public static String[] split(String paramString1, String paramString2)
  {
    return splitWorker(paramString1, paramString2, -1, false);
  }
  
  public static String[] split(String paramString1, String paramString2, int paramInt)
  {
    return splitWorker(paramString1, paramString2, paramInt, false);
  }
  
  public static String[] splitByCharacterType(String paramString)
  {
    return splitByCharacterType(paramString, false);
  }
  
  private static String[] splitByCharacterType(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return null;
    }
    if (paramString.length() == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    char[] arrayOfChar = paramString.toCharArray();
    paramString = new ArrayList();
    int i = 0;
    int j = Character.getType(arrayOfChar[0]);
    for (int k = 0 + 1; k < arrayOfChar.length; k++)
    {
      int m = Character.getType(arrayOfChar[k]);
      if (m != j)
      {
        if ((paramBoolean) && (m == 2) && (j == 1))
        {
          int n = k - 1;
          j = i;
          if (n != i)
          {
            paramString.add(new String(arrayOfChar, i, n - i));
            j = n;
          }
          i = j;
        }
        else
        {
          paramString.add(new String(arrayOfChar, i, k - i));
          i = k;
        }
        j = m;
      }
    }
    paramString.add(new String(arrayOfChar, i, arrayOfChar.length - i));
    return (String[])paramString.toArray(new String[paramString.size()]);
  }
  
  public static String[] splitByCharacterTypeCamelCase(String paramString)
  {
    return splitByCharacterType(paramString, true);
  }
  
  public static String[] splitByWholeSeparator(String paramString1, String paramString2)
  {
    return splitByWholeSeparatorWorker(paramString1, paramString2, -1, false);
  }
  
  public static String[] splitByWholeSeparator(String paramString1, String paramString2, int paramInt)
  {
    return splitByWholeSeparatorWorker(paramString1, paramString2, paramInt, false);
  }
  
  public static String[] splitByWholeSeparatorPreserveAllTokens(String paramString1, String paramString2)
  {
    return splitByWholeSeparatorWorker(paramString1, paramString2, -1, true);
  }
  
  public static String[] splitByWholeSeparatorPreserveAllTokens(String paramString1, String paramString2, int paramInt)
  {
    return splitByWholeSeparatorWorker(paramString1, paramString2, paramInt, true);
  }
  
  private static String[] splitByWholeSeparatorWorker(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (paramString1 == null) {
      return null;
    }
    int i = paramString1.length();
    if (i == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    if ((paramString2 != null) && (!"".equals(paramString2)))
    {
      int j = paramString2.length();
      ArrayList localArrayList = new ArrayList();
      int k = 0;
      int m = 0;
      int n = 0;
      while (n < i)
      {
        int i1 = paramString1.indexOf(paramString2, m);
        if (i1 > -1)
        {
          if (i1 > m)
          {
            k++;
            if (k == paramInt)
            {
              n = i;
              localArrayList.add(paramString1.substring(m));
            }
            else
            {
              localArrayList.add(paramString1.substring(m, i1));
              m = i1 + j;
              n = i1;
            }
          }
          else
          {
            int i2 = k;
            n = i1;
            if (paramBoolean)
            {
              i2 = k + 1;
              if (i2 == paramInt)
              {
                n = i;
                localArrayList.add(paramString1.substring(m));
              }
              else
              {
                localArrayList.add("");
                n = i1;
              }
            }
            m = n + j;
            k = i2;
          }
        }
        else
        {
          localArrayList.add(paramString1.substring(m));
          n = i;
        }
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    return splitWorker(paramString1, null, paramInt, paramBoolean);
  }
  
  public static String[] splitPreserveAllTokens(String paramString)
  {
    return splitWorker(paramString, null, -1, true);
  }
  
  public static String[] splitPreserveAllTokens(String paramString, char paramChar)
  {
    return splitWorker(paramString, paramChar, true);
  }
  
  public static String[] splitPreserveAllTokens(String paramString1, String paramString2)
  {
    return splitWorker(paramString1, paramString2, -1, true);
  }
  
  public static String[] splitPreserveAllTokens(String paramString1, String paramString2, int paramInt)
  {
    return splitWorker(paramString1, paramString2, paramInt, true);
  }
  
  private static String[] splitWorker(String paramString, char paramChar, boolean paramBoolean)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if (i == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (j < i) {
      if (paramString.charAt(j) == paramChar)
      {
        if ((m != 0) || (paramBoolean))
        {
          localArrayList.add(paramString.substring(k, j));
          m = 0;
          n = 1;
        }
        j++;
        k = j;
      }
      else
      {
        n = 0;
        m = 1;
        j++;
      }
    }
    if ((m != 0) || ((paramBoolean) && (n != 0))) {
      localArrayList.add(paramString.substring(k, j));
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private static String[] splitWorker(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (paramString1 == null) {
      return null;
    }
    int i = paramString1.length();
    if (i == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    ArrayList localArrayList = new ArrayList();
    int j = 1;
    int k = 1;
    int m = 1;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i10 = 0;
    int i11 = 0;
    if (paramString2 == null)
    {
      i7 = i11;
      i10 = i8;
      for (;;)
      {
        j = i2;
        n = i5;
        i3 = i10;
        i6 = i7;
        if (i2 >= i) {
          break;
        }
        if (Character.isWhitespace(paramString1.charAt(i2)))
        {
          if (i10 == 0)
          {
            j = m;
            n = i2;
            if (!paramBoolean) {}
          }
          else
          {
            i7 = 1;
            if (m == paramInt)
            {
              i7 = 0;
              i2 = i;
            }
            localArrayList.add(paramString1.substring(i5, i2));
            i10 = 0;
            j = m + 1;
            n = i2;
          }
          i2 = n + 1;
          i5 = i2;
          m = j;
        }
        else
        {
          i7 = 0;
          i10 = 1;
          i2++;
        }
      }
    }
    m = k;
    i2 = i1;
    i5 = i4;
    if (paramString2.length() == 1)
    {
      m = paramString2.charAt(0);
      i7 = i9;
      i10 = i6;
      i5 = i3;
      i2 = n;
      while (i2 < i) {
        if (paramString1.charAt(i2) == m)
        {
          if (i10 == 0)
          {
            n = j;
            i3 = i2;
            if (!paramBoolean) {}
          }
          else
          {
            i7 = 1;
            if (j == paramInt)
            {
              i7 = 0;
              i2 = i;
            }
            localArrayList.add(paramString1.substring(i5, i2));
            i10 = 0;
            n = j + 1;
            i3 = i2;
          }
          i2 = i3 + 1;
          i5 = i2;
          j = n;
        }
        else
        {
          i7 = 0;
          i10 = 1;
          i2++;
        }
      }
      j = i2;
      n = i5;
      i3 = i10;
      i6 = i7;
    }
    else
    {
      for (;;)
      {
        j = i2;
        n = i5;
        i3 = i7;
        i6 = i10;
        if (i2 >= i) {
          break;
        }
        if (paramString2.indexOf(paramString1.charAt(i2)) >= 0)
        {
          if (i7 == 0)
          {
            j = m;
            n = i2;
            if (!paramBoolean) {}
          }
          else
          {
            i7 = 1;
            if (m == paramInt)
            {
              i7 = 0;
              i2 = i;
            }
            localArrayList.add(paramString1.substring(i5, i2));
            i5 = 0;
            j = m + 1;
            i10 = i7;
            i7 = i5;
            n = i2;
          }
          i2 = n + 1;
          i5 = i2;
          m = j;
        }
        else
        {
          i10 = 0;
          i7 = 1;
          i2++;
        }
      }
    }
    if ((i3 != 0) || ((paramBoolean) && (i6 != 0))) {
      localArrayList.add(paramString1.substring(n, j));
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public static boolean startsWith(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return startsWith(paramCharSequence1, paramCharSequence2, false);
  }
  
  private static boolean startsWith(CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    boolean bool = false;
    if ((paramCharSequence1 != null) && (paramCharSequence2 != null))
    {
      if (paramCharSequence2.length() > paramCharSequence1.length()) {
        return false;
      }
      return CharSequenceUtils.regionMatches(paramCharSequence1, paramBoolean, 0, paramCharSequence2, 0, paramCharSequence2.length());
    }
    paramBoolean = bool;
    if (paramCharSequence1 == null)
    {
      paramBoolean = bool;
      if (paramCharSequence2 == null) {
        paramBoolean = true;
      }
    }
    return paramBoolean;
  }
  
  public static boolean startsWithAny(CharSequence paramCharSequence, CharSequence... paramVarArgs)
  {
    if ((!isEmpty(paramCharSequence)) && (!ArrayUtils.isEmpty(paramVarArgs)))
    {
      int i = paramVarArgs.length;
      for (int j = 0; j < i; j++) {
        if (startsWith(paramCharSequence, paramVarArgs[j])) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  public static boolean startsWithIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return startsWith(paramCharSequence1, paramCharSequence2, true);
  }
  
  public static String strip(String paramString)
  {
    return strip(paramString, null);
  }
  
  public static String strip(String paramString1, String paramString2)
  {
    if (isEmpty(paramString1)) {
      return paramString1;
    }
    return stripEnd(stripStart(paramString1, paramString2), paramString2);
  }
  
  public static String stripAccents(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      if (InitStripAccents.java6NormalizeMethod != null)
      {
        paramString = removeAccentsJava6(paramString);
      }
      else
      {
        if (InitStripAccents.sunDecomposeMethod == null) {
          break label33;
        }
        paramString = removeAccentsSUN(paramString);
      }
      return paramString;
      label33:
      UnsupportedOperationException localUnsupportedOperationException = new java/lang/UnsupportedOperationException;
      paramString = new java/lang/StringBuilder;
      paramString.<init>();
      paramString.append("The stripAccents(CharSequence) method requires at least Java6, but got: ");
      paramString.append(InitStripAccents.java6Exception);
      paramString.append("; or a Sun JVM: ");
      paramString.append(InitStripAccents.sunException);
      localUnsupportedOperationException.<init>(paramString.toString());
      throw localUnsupportedOperationException;
    }
    catch (SecurityException paramString)
    {
      throw new RuntimeException("SecurityException occurred", paramString);
    }
    catch (InvocationTargetException paramString)
    {
      throw new RuntimeException("InvocationTargetException occurred", paramString);
    }
    catch (IllegalAccessException paramString)
    {
      throw new RuntimeException("IllegalAccessException occurred", paramString);
    }
    catch (IllegalArgumentException paramString)
    {
      throw new RuntimeException("IllegalArgumentException occurred", paramString);
    }
  }
  
  public static String[] stripAll(String... paramVarArgs)
  {
    return stripAll(paramVarArgs, null);
  }
  
  public static String[] stripAll(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString != null)
    {
      int i = paramArrayOfString.length;
      if (i != 0)
      {
        String[] arrayOfString = new String[i];
        for (int j = 0; j < i; j++) {
          arrayOfString[j] = strip(paramArrayOfString[j], paramString);
        }
        return arrayOfString;
      }
    }
    return paramArrayOfString;
  }
  
  public static String stripEnd(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      int i = paramString1.length();
      int j = i;
      if (i != 0)
      {
        if (paramString2 == null) {
          for (;;)
          {
            i = j;
            if (j == 0) {
              break;
            }
            i = j;
            if (!Character.isWhitespace(paramString1.charAt(j - 1))) {
              break;
            }
            j--;
          }
        }
        if (paramString2.length() == 0) {
          return paramString1;
        }
        for (;;)
        {
          i = j;
          if (j == 0) {
            break;
          }
          i = j;
          if (paramString2.indexOf(paramString1.charAt(j - 1)) == -1) {
            break;
          }
          j--;
        }
        return paramString1.substring(0, i);
      }
    }
    return paramString1;
  }
  
  public static String stripStart(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      int i = paramString1.length();
      if (i != 0)
      {
        int j = 0;
        int k = 0;
        if (paramString2 == null) {
          for (;;)
          {
            j = k;
            if (k == i) {
              break;
            }
            j = k;
            if (!Character.isWhitespace(paramString1.charAt(k))) {
              break;
            }
            k++;
          }
        }
        k = j;
        if (paramString2.length() == 0) {
          return paramString1;
        }
        for (;;)
        {
          j = k;
          if (k == i) {
            break;
          }
          j = k;
          if (paramString2.indexOf(paramString1.charAt(k)) == -1) {
            break;
          }
          k++;
        }
        return paramString1.substring(j);
      }
    }
    return paramString1;
  }
  
  public static String stripToEmpty(String paramString)
  {
    if (paramString == null) {
      paramString = "";
    } else {
      paramString = strip(paramString, null);
    }
    return paramString;
  }
  
  public static String stripToNull(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      return null;
    }
    paramString = strip(paramString, null);
    if (paramString.length() == 0) {
      paramString = (String)localObject;
    }
    return paramString;
  }
  
  public static String substring(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = paramInt + paramString.length();
    }
    paramInt = i;
    if (i < 0) {
      paramInt = 0;
    }
    if (paramInt > paramString.length()) {
      return "";
    }
    return paramString.substring(paramInt);
  }
  
  public static String substring(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramInt2;
    if (paramInt2 < 0) {
      i = paramInt2 + paramString.length();
    }
    paramInt2 = paramInt1;
    if (paramInt1 < 0) {
      paramInt2 = paramInt1 + paramString.length();
    }
    paramInt1 = i;
    if (i > paramString.length()) {
      paramInt1 = paramString.length();
    }
    if (paramInt2 > paramInt1) {
      return "";
    }
    i = paramInt2;
    if (paramInt2 < 0) {
      i = 0;
    }
    paramInt2 = paramInt1;
    if (paramInt1 < 0) {
      paramInt2 = 0;
    }
    return paramString.substring(i, paramInt2);
  }
  
  public static String substringAfter(String paramString1, String paramString2)
  {
    if (isEmpty(paramString1)) {
      return paramString1;
    }
    if (paramString2 == null) {
      return "";
    }
    int i = paramString1.indexOf(paramString2);
    if (i == -1) {
      return "";
    }
    return paramString1.substring(paramString2.length() + i);
  }
  
  public static String substringAfterLast(String paramString1, String paramString2)
  {
    if (isEmpty(paramString1)) {
      return paramString1;
    }
    if (isEmpty(paramString2)) {
      return "";
    }
    int i = paramString1.lastIndexOf(paramString2);
    if ((i != -1) && (i != paramString1.length() - paramString2.length())) {
      return paramString1.substring(paramString2.length() + i);
    }
    return "";
  }
  
  public static String substringBefore(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (paramString2 != null))
    {
      if (paramString2.length() == 0) {
        return "";
      }
      int i = paramString1.indexOf(paramString2);
      if (i == -1) {
        return paramString1;
      }
      return paramString1.substring(0, i);
    }
    return paramString1;
  }
  
  public static String substringBeforeLast(String paramString1, String paramString2)
  {
    if ((!isEmpty(paramString1)) && (!isEmpty(paramString2)))
    {
      int i = paramString1.lastIndexOf(paramString2);
      if (i == -1) {
        return paramString1;
      }
      return paramString1.substring(0, i);
    }
    return paramString1;
  }
  
  public static String substringBetween(String paramString1, String paramString2)
  {
    return substringBetween(paramString1, paramString2, paramString2);
  }
  
  public static String substringBetween(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null))
    {
      int i = paramString1.indexOf(paramString2);
      if (i != -1)
      {
        int j = paramString1.indexOf(paramString3, paramString2.length() + i);
        if (j != -1) {
          return paramString1.substring(paramString2.length() + i, j);
        }
      }
      return null;
    }
    return null;
  }
  
  public static String[] substringsBetween(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 != null) && (!isEmpty(paramString2)) && (!isEmpty(paramString3)))
    {
      int i = paramString1.length();
      if (i == 0) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
      }
      int j = paramString3.length();
      int k = paramString2.length();
      ArrayList localArrayList = new ArrayList();
      int m = 0;
      while (m < i - j)
      {
        m = paramString1.indexOf(paramString2, m);
        if (m < 0) {
          break;
        }
        int n = m + k;
        m = paramString1.indexOf(paramString3, n);
        if (m < 0) {
          break;
        }
        localArrayList.add(paramString1.substring(n, m));
        m += j;
      }
      if (localArrayList.isEmpty()) {
        return null;
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    return null;
  }
  
  public static String swapCase(String paramString)
  {
    if (isEmpty(paramString)) {
      return paramString;
    }
    paramString = paramString.toCharArray();
    for (int i = 0; i < paramString.length; i++)
    {
      char c = paramString[i];
      if (Character.isUpperCase(c)) {
        paramString[i] = Character.toLowerCase(c);
      } else if (Character.isTitleCase(c)) {
        paramString[i] = Character.toLowerCase(c);
      } else if (Character.isLowerCase(c)) {
        paramString[i] = Character.toUpperCase(c);
      }
    }
    return new String(paramString);
  }
  
  public static String toString(byte[] paramArrayOfByte, String paramString)
    throws UnsupportedEncodingException
  {
    String str = new java/lang/String;
    if (paramString == null) {
      str.<init>(paramArrayOfByte);
    } else {
      str.<init>(paramArrayOfByte, paramString);
    }
    return str;
  }
  
  public static String trim(String paramString)
  {
    if (paramString == null) {
      paramString = null;
    } else {
      paramString = paramString.trim();
    }
    return paramString;
  }
  
  public static String trimToEmpty(String paramString)
  {
    if (paramString == null) {
      paramString = "";
    } else {
      paramString = paramString.trim();
    }
    return paramString;
  }
  
  public static String trimToNull(String paramString)
  {
    paramString = trim(paramString);
    if (isEmpty(paramString)) {
      paramString = null;
    }
    return paramString;
  }
  
  public static String uncapitalize(String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.length();
      if (i != 0)
      {
        StringBuilder localStringBuilder = new StringBuilder(i);
        localStringBuilder.append(Character.toLowerCase(paramString.charAt(0)));
        localStringBuilder.append(paramString.substring(1));
        return localStringBuilder.toString();
      }
    }
    return paramString;
  }
  
  public static String upperCase(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.toUpperCase();
  }
  
  public static String upperCase(String paramString, Locale paramLocale)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.toUpperCase(paramLocale);
  }
  
  private static class InitStripAccents
  {
    private static final Throwable java6Exception;
    private static final Method java6NormalizeMethod;
    private static final Object java6NormalizerFormNFD;
    private static final Pattern java6Pattern;
    private static final Method sunDecomposeMethod;
    private static final Throwable sunException;
    private static final Pattern sunPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    
    static
    {
      java6Pattern = sunPattern;
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Method localMethod = null;
      Object localObject4 = null;
      Object localObject5 = localObject1;
      try
      {
        Object localObject6 = Thread.currentThread().getContextClassLoader().loadClass("java.text.Normalizer$Form");
        localObject5 = localObject1;
        localObject1 = ((Class)localObject6).getField("NFD").get(null);
        localObject5 = localObject1;
        localObject6 = Thread.currentThread().getContextClassLoader().loadClass("java.text.Normalizer").getMethod("normalize", new Class[] { CharSequence.class, localObject6 });
        localObject2 = localObject6;
        localObject5 = localObject1;
        localObject1 = localMethod;
      }
      catch (Exception localException1)
      {
        try
        {
          localMethod = Thread.currentThread().getContextClassLoader().loadClass("sun.text.Normalizer").getMethod("decompose", new Class[] { String.class, Boolean.TYPE, Integer.TYPE });
          localObject3 = localMethod;
        }
        catch (Exception localException2) {}
      }
      java6Exception = localException1;
      java6NormalizerFormNFD = localObject5;
      java6NormalizeMethod = (Method)localObject2;
      sunException = localException2;
      sunDecomposeMethod = (Method)localObject3;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/StringUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */