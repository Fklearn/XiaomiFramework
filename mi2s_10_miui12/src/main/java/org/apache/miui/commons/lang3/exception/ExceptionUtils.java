package org.apache.miui.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.miui.commons.lang3.ArrayUtils;
import org.apache.miui.commons.lang3.ClassUtils;
import org.apache.miui.commons.lang3.StringUtils;
import org.apache.miui.commons.lang3.SystemUtils;

public class ExceptionUtils
{
  private static final String[] CAUSE_METHOD_NAMES = { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable" };
  static final String WRAPPED_MARKER = " [wrapped] ";
  
  @Deprecated
  public static Throwable getCause(Throwable paramThrowable)
  {
    return getCause(paramThrowable, CAUSE_METHOD_NAMES);
  }
  
  @Deprecated
  public static Throwable getCause(Throwable paramThrowable, String[] paramArrayOfString)
  {
    if (paramThrowable == null) {
      return null;
    }
    String[] arrayOfString = paramArrayOfString;
    if (paramArrayOfString == null) {}
    for (paramArrayOfString : CAUSE_METHOD_NAMES) {
      if (paramArrayOfString != null)
      {
        paramArrayOfString = getCauseUsingMethodName(paramThrowable, paramArrayOfString);
        if (paramArrayOfString != null) {
          return paramArrayOfString;
        }
      }
    }
    return null;
  }
  
  private static Throwable getCauseUsingMethodName(Throwable paramThrowable, String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      paramString = paramThrowable.getClass().getMethod(paramString, new Class[0]);
    }
    catch (SecurityException paramString)
    {
      paramString = (String)localObject1;
    }
    catch (NoSuchMethodException paramString)
    {
      for (;;)
      {
        paramString = (String)localObject2;
      }
    }
    if ((paramString != null) && (Throwable.class.isAssignableFrom(paramString.getReturnType()))) {
      try
      {
        paramThrowable = (Throwable)paramString.invoke(paramThrowable, new Object[0]);
        return paramThrowable;
      }
      catch (InvocationTargetException paramThrowable) {}catch (IllegalArgumentException paramThrowable) {}catch (IllegalAccessException paramThrowable) {}
    }
    return null;
  }
  
  @Deprecated
  public static String[] getDefaultCauseMethodNames()
  {
    return (String[])ArrayUtils.clone(CAUSE_METHOD_NAMES);
  }
  
  public static String getMessage(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return "";
    }
    String str1 = ClassUtils.getShortClassName(paramThrowable, null);
    String str2 = paramThrowable.getMessage();
    paramThrowable = new StringBuilder();
    paramThrowable.append(str1);
    paramThrowable.append(": ");
    paramThrowable.append(StringUtils.defaultString(str2));
    return paramThrowable.toString();
  }
  
  public static Throwable getRootCause(Throwable paramThrowable)
  {
    paramThrowable = getThrowableList(paramThrowable);
    if (paramThrowable.size() < 2) {
      paramThrowable = null;
    } else {
      paramThrowable = (Throwable)paramThrowable.get(paramThrowable.size() - 1);
    }
    return paramThrowable;
  }
  
  public static String getRootCauseMessage(Throwable paramThrowable)
  {
    Throwable localThrowable = getRootCause(paramThrowable);
    if (localThrowable != null) {
      paramThrowable = localThrowable;
    }
    return getMessage(paramThrowable);
  }
  
  public static String[] getRootCauseStackTrace(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    Throwable[] arrayOfThrowable = getThrowables(paramThrowable);
    int i = arrayOfThrowable.length;
    ArrayList localArrayList = new ArrayList();
    paramThrowable = getStackFrameList(arrayOfThrowable[(i - 1)]);
    int k;
    for (int j = i;; j = k)
    {
      Throwable localThrowable = paramThrowable;
      k = j - 1;
      if (k < 0) {
        break;
      }
      paramThrowable = localThrowable;
      if (k != 0)
      {
        paramThrowable = getStackFrameList(arrayOfThrowable[(k - 1)]);
        removeCommonFrames(localThrowable, paramThrowable);
      }
      if (k == i - 1)
      {
        localArrayList.add(arrayOfThrowable[k].toString());
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(" [wrapped] ");
        localStringBuilder.append(arrayOfThrowable[k].toString());
        localArrayList.add(localStringBuilder.toString());
      }
      for (j = 0; j < localThrowable.size(); j++) {
        localArrayList.add((String)localThrowable.get(j));
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  static List<String> getStackFrameList(Throwable paramThrowable)
  {
    paramThrowable = new StringTokenizer(getStackTrace(paramThrowable), SystemUtils.LINE_SEPARATOR);
    ArrayList localArrayList = new ArrayList();
    int j;
    for (int i = 0; paramThrowable.hasMoreTokens(); i = j)
    {
      String str = paramThrowable.nextToken();
      j = str.indexOf("at");
      if ((j != -1) && (str.substring(0, j).trim().length() == 0))
      {
        j = 1;
        localArrayList.add(str);
      }
      else
      {
        j = i;
        if (i != 0) {
          break;
        }
      }
    }
    return localArrayList;
  }
  
  static String[] getStackFrames(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, SystemUtils.LINE_SEPARATOR);
    paramString = new ArrayList();
    while (localStringTokenizer.hasMoreTokens()) {
      paramString.add(localStringTokenizer.nextToken());
    }
    return (String[])paramString.toArray(new String[paramString.size()]);
  }
  
  public static String[] getStackFrames(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return getStackFrames(getStackTrace(paramThrowable));
  }
  
  public static String getStackTrace(Throwable paramThrowable)
  {
    StringWriter localStringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(localStringWriter, true));
    return localStringWriter.getBuffer().toString();
  }
  
  public static int getThrowableCount(Throwable paramThrowable)
  {
    return getThrowableList(paramThrowable).size();
  }
  
  public static List<Throwable> getThrowableList(Throwable paramThrowable)
  {
    ArrayList localArrayList = new ArrayList();
    while ((paramThrowable != null) && (!localArrayList.contains(paramThrowable)))
    {
      localArrayList.add(paramThrowable);
      paramThrowable = getCause(paramThrowable);
    }
    return localArrayList;
  }
  
  public static Throwable[] getThrowables(Throwable paramThrowable)
  {
    paramThrowable = getThrowableList(paramThrowable);
    return (Throwable[])paramThrowable.toArray(new Throwable[paramThrowable.size()]);
  }
  
  private static int indexOf(Throwable paramThrowable, Class<?> paramClass, int paramInt, boolean paramBoolean)
  {
    if ((paramThrowable != null) && (paramClass != null))
    {
      int i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      paramThrowable = getThrowables(paramThrowable);
      if (i >= paramThrowable.length) {
        return -1;
      }
      if (paramBoolean) {
        for (paramInt = i; paramInt < paramThrowable.length; paramInt++) {
          if (paramClass.isAssignableFrom(paramThrowable[paramInt].getClass())) {
            return paramInt;
          }
        }
      } else {
        for (paramInt = i; paramInt < paramThrowable.length; paramInt++) {
          if (paramClass.equals(paramThrowable[paramInt].getClass())) {
            return paramInt;
          }
        }
      }
      return -1;
    }
    return -1;
  }
  
  public static int indexOfThrowable(Throwable paramThrowable, Class<?> paramClass)
  {
    return indexOf(paramThrowable, paramClass, 0, false);
  }
  
  public static int indexOfThrowable(Throwable paramThrowable, Class<?> paramClass, int paramInt)
  {
    return indexOf(paramThrowable, paramClass, paramInt, false);
  }
  
  public static int indexOfType(Throwable paramThrowable, Class<?> paramClass)
  {
    return indexOf(paramThrowable, paramClass, 0, true);
  }
  
  public static int indexOfType(Throwable paramThrowable, Class<?> paramClass, int paramInt)
  {
    return indexOf(paramThrowable, paramClass, paramInt, true);
  }
  
  public static void printRootCauseStackTrace(Throwable paramThrowable)
  {
    printRootCauseStackTrace(paramThrowable, System.err);
  }
  
  public static void printRootCauseStackTrace(Throwable paramThrowable, PrintStream paramPrintStream)
  {
    if (paramThrowable == null) {
      return;
    }
    if (paramPrintStream != null)
    {
      paramThrowable = getRootCauseStackTrace(paramThrowable);
      int i = paramThrowable.length;
      for (int j = 0; j < i; j++) {
        paramPrintStream.println(paramThrowable[j]);
      }
      paramPrintStream.flush();
      return;
    }
    throw new IllegalArgumentException("The PrintStream must not be null");
  }
  
  public static void printRootCauseStackTrace(Throwable paramThrowable, PrintWriter paramPrintWriter)
  {
    if (paramThrowable == null) {
      return;
    }
    if (paramPrintWriter != null)
    {
      paramThrowable = getRootCauseStackTrace(paramThrowable);
      int i = paramThrowable.length;
      for (int j = 0; j < i; j++) {
        paramPrintWriter.println(paramThrowable[j]);
      }
      paramPrintWriter.flush();
      return;
    }
    throw new IllegalArgumentException("The PrintWriter must not be null");
  }
  
  public static void removeCommonFrames(List<String> paramList1, List<String> paramList2)
  {
    if ((paramList1 != null) && (paramList2 != null))
    {
      int i = paramList1.size() - 1;
      for (int j = paramList2.size() - 1; (i >= 0) && (j >= 0); j--)
      {
        if (((String)paramList1.get(i)).equals((String)paramList2.get(j))) {
          paramList1.remove(i);
        }
        i--;
      }
      return;
    }
    throw new IllegalArgumentException("The List must not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/exception/ExceptionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */