package org.apache.miui.commons.lang3.builder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.miui.commons.lang3.ClassUtils;
import org.apache.miui.commons.lang3.ObjectUtils;
import org.apache.miui.commons.lang3.SystemUtils;

public abstract class ToStringStyle
  implements Serializable
{
  public static final ToStringStyle DEFAULT_STYLE = new DefaultToStringStyle();
  public static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();
  public static final ToStringStyle NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();
  private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY = new ThreadLocal();
  public static final ToStringStyle SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();
  public static final ToStringStyle SIMPLE_STYLE = new SimpleToStringStyle();
  private static final long serialVersionUID = -2587890625525655916L;
  private boolean arrayContentDetail = true;
  private String arrayEnd = "}";
  private String arraySeparator = ",";
  private String arrayStart = "{";
  private String contentEnd = "]";
  private String contentStart = "[";
  private boolean defaultFullDetail = true;
  private String fieldNameValueSeparator = "=";
  private String fieldSeparator = ",";
  private boolean fieldSeparatorAtEnd = false;
  private boolean fieldSeparatorAtStart = false;
  private String nullText = "<null>";
  private String sizeEndText = ">";
  private String sizeStartText = "<size=";
  private String summaryObjectEndText = ">";
  private String summaryObjectStartText = "<";
  private boolean useClassName = true;
  private boolean useFieldNames = true;
  private boolean useIdentityHashCode = true;
  private boolean useShortClassName = false;
  
  static Map<Object, Object> getRegistry()
  {
    return (Map)REGISTRY.get();
  }
  
  static boolean isRegistered(Object paramObject)
  {
    Map localMap = getRegistry();
    boolean bool;
    if ((localMap != null) && (localMap.containsKey(paramObject))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static void register(Object paramObject)
  {
    if (paramObject != null)
    {
      if (getRegistry() == null) {
        REGISTRY.set(new WeakHashMap());
      }
      getRegistry().put(paramObject, null);
    }
  }
  
  static void unregister(Object paramObject)
  {
    if (paramObject != null)
    {
      Map localMap = getRegistry();
      if (localMap != null)
      {
        localMap.remove(paramObject);
        if (localMap.isEmpty()) {
          REGISTRY.remove();
        }
      }
    }
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, byte paramByte)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramByte);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, char paramChar)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramChar);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, double paramDouble)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramDouble);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, float paramFloat)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramFloat);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, int paramInt)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramInt);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, long paramLong)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramLong);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, Object paramObject, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramObject == null) {
      appendNullText(paramStringBuffer, paramString);
    } else {
      appendInternal(paramStringBuffer, paramString, paramObject, isFullDetail(paramBoolean));
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, short paramShort)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramShort);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    appendDetail(paramStringBuffer, paramString, paramBoolean);
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, byte[] paramArrayOfByte, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfByte == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfByte);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfByte);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, char[] paramArrayOfChar, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfChar == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfChar);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfChar);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, double[] paramArrayOfDouble, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfDouble == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfDouble);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfDouble);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, float[] paramArrayOfFloat, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfFloat == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfFloat);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfFloat);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, int[] paramArrayOfInt, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfInt == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfInt);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfInt);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, long[] paramArrayOfLong, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfLong == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfLong);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfLong);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, Object[] paramArrayOfObject, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfObject == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfObject);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfObject);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, short[] paramArrayOfShort, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfShort == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfShort);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfShort);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  public void append(StringBuffer paramStringBuffer, String paramString, boolean[] paramArrayOfBoolean, Boolean paramBoolean)
  {
    appendFieldStart(paramStringBuffer, paramString);
    if (paramArrayOfBoolean == null) {
      appendNullText(paramStringBuffer, paramString);
    } else if (isFullDetail(paramBoolean)) {
      appendDetail(paramStringBuffer, paramString, paramArrayOfBoolean);
    } else {
      appendSummary(paramStringBuffer, paramString, paramArrayOfBoolean);
    }
    appendFieldEnd(paramStringBuffer, paramString);
  }
  
  protected void appendClassName(StringBuffer paramStringBuffer, Object paramObject)
  {
    if ((this.useClassName) && (paramObject != null))
    {
      register(paramObject);
      if (this.useShortClassName) {
        paramStringBuffer.append(getShortClassName(paramObject.getClass()));
      } else {
        paramStringBuffer.append(paramObject.getClass().getName());
      }
    }
  }
  
  protected void appendContentEnd(StringBuffer paramStringBuffer)
  {
    paramStringBuffer.append(this.contentEnd);
  }
  
  protected void appendContentStart(StringBuffer paramStringBuffer)
  {
    paramStringBuffer.append(this.contentStart);
  }
  
  protected void appendCyclicObject(StringBuffer paramStringBuffer, String paramString, Object paramObject)
  {
    ObjectUtils.identityToString(paramStringBuffer, paramObject);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, byte paramByte)
  {
    paramStringBuffer.append(paramByte);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, char paramChar)
  {
    paramStringBuffer.append(paramChar);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, double paramDouble)
  {
    paramStringBuffer.append(paramDouble);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, float paramFloat)
  {
    paramStringBuffer.append(paramFloat);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, int paramInt)
  {
    paramStringBuffer.append(paramInt);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, long paramLong)
  {
    paramStringBuffer.append(paramLong);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, Object paramObject)
  {
    paramStringBuffer.append(paramObject);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, Collection<?> paramCollection)
  {
    paramStringBuffer.append(paramCollection);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, Map<?, ?> paramMap)
  {
    paramStringBuffer.append(paramMap);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, short paramShort)
  {
    paramStringBuffer.append(paramShort);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean)
  {
    paramStringBuffer.append(paramBoolean);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, byte[] paramArrayOfByte)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfByte[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, char[] paramArrayOfChar)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfChar.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfChar[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, double[] paramArrayOfDouble)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfDouble.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfDouble[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, float[] paramArrayOfFloat)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfFloat[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, int[] paramArrayOfInt)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfInt[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, long[] paramArrayOfLong)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfLong[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, Object[] paramArrayOfObject)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      Object localObject = paramArrayOfObject[i];
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      if (localObject == null) {
        appendNullText(paramStringBuffer, paramString);
      } else {
        appendInternal(paramStringBuffer, paramString, localObject, this.arrayContentDetail);
      }
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, short[] paramArrayOfShort)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfShort.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfShort[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void appendDetail(StringBuffer paramStringBuffer, String paramString, boolean[] paramArrayOfBoolean)
  {
    paramStringBuffer.append(this.arrayStart);
    for (int i = 0; i < paramArrayOfBoolean.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      appendDetail(paramStringBuffer, paramString, paramArrayOfBoolean[i]);
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  public void appendEnd(StringBuffer paramStringBuffer, Object paramObject)
  {
    if (!this.fieldSeparatorAtEnd) {
      removeLastFieldSeparator(paramStringBuffer);
    }
    appendContentEnd(paramStringBuffer);
    unregister(paramObject);
  }
  
  protected void appendFieldEnd(StringBuffer paramStringBuffer, String paramString)
  {
    appendFieldSeparator(paramStringBuffer);
  }
  
  protected void appendFieldSeparator(StringBuffer paramStringBuffer)
  {
    paramStringBuffer.append(this.fieldSeparator);
  }
  
  protected void appendFieldStart(StringBuffer paramStringBuffer, String paramString)
  {
    if ((this.useFieldNames) && (paramString != null))
    {
      paramStringBuffer.append(paramString);
      paramStringBuffer.append(this.fieldNameValueSeparator);
    }
  }
  
  protected void appendIdentityHashCode(StringBuffer paramStringBuffer, Object paramObject)
  {
    if ((isUseIdentityHashCode()) && (paramObject != null))
    {
      register(paramObject);
      paramStringBuffer.append('@');
      paramStringBuffer.append(Integer.toHexString(System.identityHashCode(paramObject)));
    }
  }
  
  protected void appendInternal(StringBuffer paramStringBuffer, String paramString, Object paramObject, boolean paramBoolean)
  {
    if ((isRegistered(paramObject)) && (!(paramObject instanceof Number)) && (!(paramObject instanceof Boolean)) && (!(paramObject instanceof Character)))
    {
      appendCyclicObject(paramStringBuffer, paramString, paramObject);
      return;
    }
    register(paramObject);
    try
    {
      if ((paramObject instanceof Collection))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (Collection)paramObject);
        } else {
          appendSummarySize(paramStringBuffer, paramString, ((Collection)paramObject).size());
        }
      }
      else if ((paramObject instanceof Map))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (Map)paramObject);
        } else {
          appendSummarySize(paramStringBuffer, paramString, ((Map)paramObject).size());
        }
      }
      else if ((paramObject instanceof long[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (long[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (long[])paramObject);
        }
      }
      else if ((paramObject instanceof int[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (int[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (int[])paramObject);
        }
      }
      else if ((paramObject instanceof short[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (short[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (short[])paramObject);
        }
      }
      else if ((paramObject instanceof byte[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (byte[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (byte[])paramObject);
        }
      }
      else if ((paramObject instanceof char[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (char[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (char[])paramObject);
        }
      }
      else if ((paramObject instanceof double[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (double[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (double[])paramObject);
        }
      }
      else if ((paramObject instanceof float[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (float[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (float[])paramObject);
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (boolean[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (boolean[])paramObject);
        }
      }
      else if (paramObject.getClass().isArray())
      {
        if (paramBoolean) {
          appendDetail(paramStringBuffer, paramString, (Object[])paramObject);
        } else {
          appendSummary(paramStringBuffer, paramString, (Object[])paramObject);
        }
      }
      else if (paramBoolean) {
        appendDetail(paramStringBuffer, paramString, paramObject);
      } else {
        appendSummary(paramStringBuffer, paramString, paramObject);
      }
      return;
    }
    finally
    {
      unregister(paramObject);
    }
  }
  
  protected void appendNullText(StringBuffer paramStringBuffer, String paramString)
  {
    paramStringBuffer.append(this.nullText);
  }
  
  public void appendStart(StringBuffer paramStringBuffer, Object paramObject)
  {
    if (paramObject != null)
    {
      appendClassName(paramStringBuffer, paramObject);
      appendIdentityHashCode(paramStringBuffer, paramObject);
      appendContentStart(paramStringBuffer);
      if (this.fieldSeparatorAtStart) {
        appendFieldSeparator(paramStringBuffer);
      }
    }
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, Object paramObject)
  {
    paramStringBuffer.append(this.summaryObjectStartText);
    paramStringBuffer.append(getShortClassName(paramObject.getClass()));
    paramStringBuffer.append(this.summaryObjectEndText);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, byte[] paramArrayOfByte)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfByte.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, char[] paramArrayOfChar)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfChar.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, double[] paramArrayOfDouble)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfDouble.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, float[] paramArrayOfFloat)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfFloat.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, int[] paramArrayOfInt)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfInt.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, long[] paramArrayOfLong)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfLong.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, Object[] paramArrayOfObject)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfObject.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, short[] paramArrayOfShort)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfShort.length);
  }
  
  protected void appendSummary(StringBuffer paramStringBuffer, String paramString, boolean[] paramArrayOfBoolean)
  {
    appendSummarySize(paramStringBuffer, paramString, paramArrayOfBoolean.length);
  }
  
  protected void appendSummarySize(StringBuffer paramStringBuffer, String paramString, int paramInt)
  {
    paramStringBuffer.append(this.sizeStartText);
    paramStringBuffer.append(paramInt);
    paramStringBuffer.append(this.sizeEndText);
  }
  
  public void appendSuper(StringBuffer paramStringBuffer, String paramString)
  {
    appendToString(paramStringBuffer, paramString);
  }
  
  public void appendToString(StringBuffer paramStringBuffer, String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.indexOf(this.contentStart) + this.contentStart.length();
      int j = paramString.lastIndexOf(this.contentEnd);
      if ((i != j) && (i >= 0) && (j >= 0))
      {
        paramString = paramString.substring(i, j);
        if (this.fieldSeparatorAtStart) {
          removeLastFieldSeparator(paramStringBuffer);
        }
        paramStringBuffer.append(paramString);
        appendFieldSeparator(paramStringBuffer);
      }
    }
  }
  
  protected String getArrayEnd()
  {
    return this.arrayEnd;
  }
  
  protected String getArraySeparator()
  {
    return this.arraySeparator;
  }
  
  protected String getArrayStart()
  {
    return this.arrayStart;
  }
  
  protected String getContentEnd()
  {
    return this.contentEnd;
  }
  
  protected String getContentStart()
  {
    return this.contentStart;
  }
  
  protected String getFieldNameValueSeparator()
  {
    return this.fieldNameValueSeparator;
  }
  
  protected String getFieldSeparator()
  {
    return this.fieldSeparator;
  }
  
  protected String getNullText()
  {
    return this.nullText;
  }
  
  protected String getShortClassName(Class<?> paramClass)
  {
    return ClassUtils.getShortClassName(paramClass);
  }
  
  protected String getSizeEndText()
  {
    return this.sizeEndText;
  }
  
  protected String getSizeStartText()
  {
    return this.sizeStartText;
  }
  
  protected String getSummaryObjectEndText()
  {
    return this.summaryObjectEndText;
  }
  
  protected String getSummaryObjectStartText()
  {
    return this.summaryObjectStartText;
  }
  
  protected boolean isArrayContentDetail()
  {
    return this.arrayContentDetail;
  }
  
  protected boolean isDefaultFullDetail()
  {
    return this.defaultFullDetail;
  }
  
  protected boolean isFieldSeparatorAtEnd()
  {
    return this.fieldSeparatorAtEnd;
  }
  
  protected boolean isFieldSeparatorAtStart()
  {
    return this.fieldSeparatorAtStart;
  }
  
  protected boolean isFullDetail(Boolean paramBoolean)
  {
    if (paramBoolean == null) {
      return this.defaultFullDetail;
    }
    return paramBoolean.booleanValue();
  }
  
  protected boolean isUseClassName()
  {
    return this.useClassName;
  }
  
  protected boolean isUseFieldNames()
  {
    return this.useFieldNames;
  }
  
  protected boolean isUseIdentityHashCode()
  {
    return this.useIdentityHashCode;
  }
  
  protected boolean isUseShortClassName()
  {
    return this.useShortClassName;
  }
  
  protected void reflectionAppendArrayDetail(StringBuffer paramStringBuffer, String paramString, Object paramObject)
  {
    paramStringBuffer.append(this.arrayStart);
    int i = Array.getLength(paramObject);
    for (int j = 0; j < i; j++)
    {
      Object localObject = Array.get(paramObject, j);
      if (j > 0) {
        paramStringBuffer.append(this.arraySeparator);
      }
      if (localObject == null) {
        appendNullText(paramStringBuffer, paramString);
      } else {
        appendInternal(paramStringBuffer, paramString, localObject, this.arrayContentDetail);
      }
    }
    paramStringBuffer.append(this.arrayEnd);
  }
  
  protected void removeLastFieldSeparator(StringBuffer paramStringBuffer)
  {
    int i = paramStringBuffer.length();
    int j = this.fieldSeparator.length();
    if ((i > 0) && (j > 0) && (i >= j))
    {
      int k = 1;
      int n;
      for (int m = 0;; m++)
      {
        n = k;
        if (m >= j) {
          break;
        }
        if (paramStringBuffer.charAt(i - 1 - m) != this.fieldSeparator.charAt(j - 1 - m))
        {
          n = 0;
          break;
        }
      }
      if (n != 0) {
        paramStringBuffer.setLength(i - j);
      }
    }
  }
  
  protected void setArrayContentDetail(boolean paramBoolean)
  {
    this.arrayContentDetail = paramBoolean;
  }
  
  protected void setArrayEnd(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.arrayEnd = str;
  }
  
  protected void setArraySeparator(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.arraySeparator = str;
  }
  
  protected void setArrayStart(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.arrayStart = str;
  }
  
  protected void setContentEnd(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.contentEnd = str;
  }
  
  protected void setContentStart(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.contentStart = str;
  }
  
  protected void setDefaultFullDetail(boolean paramBoolean)
  {
    this.defaultFullDetail = paramBoolean;
  }
  
  protected void setFieldNameValueSeparator(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.fieldNameValueSeparator = str;
  }
  
  protected void setFieldSeparator(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.fieldSeparator = str;
  }
  
  protected void setFieldSeparatorAtEnd(boolean paramBoolean)
  {
    this.fieldSeparatorAtEnd = paramBoolean;
  }
  
  protected void setFieldSeparatorAtStart(boolean paramBoolean)
  {
    this.fieldSeparatorAtStart = paramBoolean;
  }
  
  protected void setNullText(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.nullText = str;
  }
  
  protected void setSizeEndText(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.sizeEndText = str;
  }
  
  protected void setSizeStartText(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.sizeStartText = str;
  }
  
  protected void setSummaryObjectEndText(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.summaryObjectEndText = str;
  }
  
  protected void setSummaryObjectStartText(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.summaryObjectStartText = str;
  }
  
  protected void setUseClassName(boolean paramBoolean)
  {
    this.useClassName = paramBoolean;
  }
  
  protected void setUseFieldNames(boolean paramBoolean)
  {
    this.useFieldNames = paramBoolean;
  }
  
  protected void setUseIdentityHashCode(boolean paramBoolean)
  {
    this.useIdentityHashCode = paramBoolean;
  }
  
  protected void setUseShortClassName(boolean paramBoolean)
  {
    this.useShortClassName = paramBoolean;
  }
  
  private static final class DefaultToStringStyle
    extends ToStringStyle
  {
    private static final long serialVersionUID = 1L;
    
    private Object readResolve()
    {
      return ToStringStyle.DEFAULT_STYLE;
    }
  }
  
  private static final class MultiLineToStringStyle
    extends ToStringStyle
  {
    private static final long serialVersionUID = 1L;
    
    MultiLineToStringStyle()
    {
      setContentStart("[");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(SystemUtils.LINE_SEPARATOR);
      localStringBuilder.append("  ");
      setFieldSeparator(localStringBuilder.toString());
      setFieldSeparatorAtStart(true);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(SystemUtils.LINE_SEPARATOR);
      localStringBuilder.append("]");
      setContentEnd(localStringBuilder.toString());
    }
    
    private Object readResolve()
    {
      return ToStringStyle.MULTI_LINE_STYLE;
    }
  }
  
  private static final class NoFieldNameToStringStyle
    extends ToStringStyle
  {
    private static final long serialVersionUID = 1L;
    
    NoFieldNameToStringStyle()
    {
      setUseFieldNames(false);
    }
    
    private Object readResolve()
    {
      return ToStringStyle.NO_FIELD_NAMES_STYLE;
    }
  }
  
  private static final class ShortPrefixToStringStyle
    extends ToStringStyle
  {
    private static final long serialVersionUID = 1L;
    
    ShortPrefixToStringStyle()
    {
      setUseShortClassName(true);
      setUseIdentityHashCode(false);
    }
    
    private Object readResolve()
    {
      return ToStringStyle.SHORT_PREFIX_STYLE;
    }
  }
  
  private static final class SimpleToStringStyle
    extends ToStringStyle
  {
    private static final long serialVersionUID = 1L;
    
    SimpleToStringStyle()
    {
      setUseClassName(false);
      setUseIdentityHashCode(false);
      setUseFieldNames(false);
      setContentStart("");
      setContentEnd("");
    }
    
    private Object readResolve()
    {
      return ToStringStyle.SIMPLE_STYLE;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/ToStringStyle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */