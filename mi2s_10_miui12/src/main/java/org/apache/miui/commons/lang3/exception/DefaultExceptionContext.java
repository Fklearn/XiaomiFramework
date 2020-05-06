package org.apache.miui.commons.lang3.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.miui.commons.lang3.StringUtils;
import org.apache.miui.commons.lang3.tuple.ImmutablePair;
import org.apache.miui.commons.lang3.tuple.Pair;

public class DefaultExceptionContext
  implements ExceptionContext, Serializable
{
  private static final long serialVersionUID = 20110706L;
  private final List<Pair<String, Object>> contextValues = new ArrayList();
  
  public DefaultExceptionContext addContextValue(String paramString, Object paramObject)
  {
    this.contextValues.add(new ImmutablePair(paramString, paramObject));
    return this;
  }
  
  public List<Pair<String, Object>> getContextEntries()
  {
    return this.contextValues;
  }
  
  public Set<String> getContextLabels()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = this.contextValues.iterator();
    while (localIterator.hasNext()) {
      localHashSet.add((String)((Pair)localIterator.next()).getKey());
    }
    return localHashSet;
  }
  
  public List<Object> getContextValues(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.contextValues.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if (StringUtils.equals(paramString, (CharSequence)localPair.getKey())) {
        localArrayList.add(localPair.getValue());
      }
    }
    return localArrayList;
  }
  
  public Object getFirstContextValue(String paramString)
  {
    Iterator localIterator = this.contextValues.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if (StringUtils.equals(paramString, (CharSequence)localPair.getKey())) {
        return localPair.getValue();
      }
    }
    return null;
  }
  
  public String getFormattedExceptionMessage(String paramString)
  {
    StringBuilder localStringBuilder1 = new StringBuilder(256);
    if (paramString != null) {
      localStringBuilder1.append(paramString);
    }
    if (this.contextValues.size() > 0)
    {
      if (localStringBuilder1.length() > 0) {
        localStringBuilder1.append('\n');
      }
      localStringBuilder1.append("Exception Context:\n");
      int i = 0;
      Iterator localIterator = this.contextValues.iterator();
      while (localIterator.hasNext())
      {
        paramString = (Pair)localIterator.next();
        localStringBuilder1.append("\t[");
        i++;
        localStringBuilder1.append(i);
        localStringBuilder1.append(':');
        localStringBuilder1.append((String)paramString.getKey());
        localStringBuilder1.append("=");
        paramString = paramString.getValue();
        if (paramString == null)
        {
          localStringBuilder1.append("null");
        }
        else
        {
          try
          {
            paramString = paramString.toString();
          }
          catch (Exception paramString)
          {
            StringBuilder localStringBuilder2 = new StringBuilder();
            localStringBuilder2.append("Exception thrown on toString(): ");
            localStringBuilder2.append(ExceptionUtils.getStackTrace(paramString));
            paramString = localStringBuilder2.toString();
          }
          localStringBuilder1.append(paramString);
        }
        localStringBuilder1.append("]\n");
      }
      localStringBuilder1.append("---------------------------------");
    }
    return localStringBuilder1.toString();
  }
  
  public DefaultExceptionContext setContextValue(String paramString, Object paramObject)
  {
    Iterator localIterator = this.contextValues.iterator();
    while (localIterator.hasNext()) {
      if (StringUtils.equals(paramString, (CharSequence)((Pair)localIterator.next()).getKey())) {
        localIterator.remove();
      }
    }
    addContextValue(paramString, paramObject);
    return this;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/exception/DefaultExceptionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */