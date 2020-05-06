package android.view.inputmethod;

import android.os.Bundle;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class InputConnectionInspector
{
  private static final Map<Class, Integer> sMissingMethodsMap = Collections.synchronizedMap(new WeakHashMap());
  
  public static int getMissingMethodFlags(InputConnection paramInputConnection)
  {
    if (paramInputConnection == null) {
      return 0;
    }
    if ((paramInputConnection instanceof BaseInputConnection)) {
      return 0;
    }
    if ((paramInputConnection instanceof InputConnectionWrapper)) {
      return ((InputConnectionWrapper)paramInputConnection).getMissingMethodFlags();
    }
    return getMissingMethodFlagsInternal(paramInputConnection.getClass());
  }
  
  public static String getMissingMethodFlagsAsString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    if ((paramInt & 0x1) != 0)
    {
      localStringBuilder.append("getSelectedText(int)");
      i = 0;
    }
    int j = i;
    if ((paramInt & 0x2) != 0)
    {
      if (i == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("setComposingRegion(int, int)");
      j = 0;
    }
    i = j;
    if ((paramInt & 0x4) != 0)
    {
      if (j == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("commitCorrection(CorrectionInfo)");
      i = 0;
    }
    j = i;
    if ((paramInt & 0x8) != 0)
    {
      if (i == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("requestCursorUpdate(int)");
      j = 0;
    }
    i = j;
    if ((paramInt & 0x10) != 0)
    {
      if (j == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("deleteSurroundingTextInCodePoints(int, int)");
      i = 0;
    }
    if ((paramInt & 0x20) != 0)
    {
      if (i == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("getHandler()");
    }
    if ((paramInt & 0x40) != 0)
    {
      if (i == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("closeConnection()");
    }
    if ((paramInt & 0x80) != 0)
    {
      if (i == 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append("commitContent(InputContentInfo, Bundle)");
    }
    return localStringBuilder.toString();
  }
  
  public static int getMissingMethodFlagsInternal(Class paramClass)
  {
    Integer localInteger = (Integer)sMissingMethodsMap.get(paramClass);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int i = 0;
    if (!hasGetSelectedText(paramClass)) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (!hasSetComposingRegion(paramClass)) {
      j = i | 0x2;
    }
    i = j;
    if (!hasCommitCorrection(paramClass)) {
      i = j | 0x4;
    }
    j = i;
    if (!hasRequestCursorUpdate(paramClass)) {
      j = i | 0x8;
    }
    i = j;
    if (!hasDeleteSurroundingTextInCodePoints(paramClass)) {
      i = j | 0x10;
    }
    j = i;
    if (!hasGetHandler(paramClass)) {
      j = i | 0x20;
    }
    i = j;
    if (!hasCloseConnection(paramClass)) {
      i = j | 0x40;
    }
    j = i;
    if (!hasCommitContent(paramClass)) {
      j = i | 0x80;
    }
    sMissingMethodsMap.put(paramClass, Integer.valueOf(j));
    return j;
  }
  
  private static boolean hasCloseConnection(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("closeConnection", new Class[0]).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasCommitContent(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("commitContent", new Class[] { InputContentInfo.class, Integer.TYPE, Bundle.class }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasCommitCorrection(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("commitCorrection", new Class[] { CorrectionInfo.class }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasDeleteSurroundingTextInCodePoints(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("deleteSurroundingTextInCodePoints", new Class[] { Integer.TYPE, Integer.TYPE }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasGetHandler(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("getHandler", new Class[0]).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasGetSelectedText(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("getSelectedText", new Class[] { Integer.TYPE }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasRequestCursorUpdate(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("requestCursorUpdates", new Class[] { Integer.TYPE }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  private static boolean hasSetComposingRegion(Class paramClass)
  {
    try
    {
      boolean bool = Modifier.isAbstract(paramClass.getMethod("setComposingRegion", new Class[] { Integer.TYPE, Integer.TYPE }).getModifiers());
      return bool ^ true;
    }
    catch (NoSuchMethodException paramClass) {}
    return false;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface MissingMethodFlags
  {
    public static final int CLOSE_CONNECTION = 64;
    public static final int COMMIT_CONTENT = 128;
    public static final int COMMIT_CORRECTION = 4;
    public static final int DELETE_SURROUNDING_TEXT_IN_CODE_POINTS = 16;
    public static final int GET_HANDLER = 32;
    public static final int GET_SELECTED_TEXT = 1;
    public static final int REQUEST_CURSOR_UPDATES = 8;
    public static final int SET_COMPOSING_REGION = 2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputConnectionInspector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */