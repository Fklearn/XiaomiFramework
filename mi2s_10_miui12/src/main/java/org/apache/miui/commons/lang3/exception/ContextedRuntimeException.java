package org.apache.miui.commons.lang3.exception;

import java.util.List;
import java.util.Set;
import org.apache.miui.commons.lang3.tuple.Pair;

public class ContextedRuntimeException
  extends RuntimeException
  implements ExceptionContext
{
  private static final long serialVersionUID = 20110706L;
  private final ExceptionContext exceptionContext;
  
  public ContextedRuntimeException()
  {
    this.exceptionContext = new DefaultExceptionContext();
  }
  
  public ContextedRuntimeException(String paramString)
  {
    super(paramString);
    this.exceptionContext = new DefaultExceptionContext();
  }
  
  public ContextedRuntimeException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.exceptionContext = new DefaultExceptionContext();
  }
  
  public ContextedRuntimeException(String paramString, Throwable paramThrowable, ExceptionContext paramExceptionContext)
  {
    super(paramString, paramThrowable);
    paramString = paramExceptionContext;
    if (paramExceptionContext == null) {
      paramString = new DefaultExceptionContext();
    }
    this.exceptionContext = paramString;
  }
  
  public ContextedRuntimeException(Throwable paramThrowable)
  {
    super(paramThrowable);
    this.exceptionContext = new DefaultExceptionContext();
  }
  
  public ContextedRuntimeException addContextValue(String paramString, Object paramObject)
  {
    this.exceptionContext.addContextValue(paramString, paramObject);
    return this;
  }
  
  public List<Pair<String, Object>> getContextEntries()
  {
    return this.exceptionContext.getContextEntries();
  }
  
  public Set<String> getContextLabels()
  {
    return this.exceptionContext.getContextLabels();
  }
  
  public List<Object> getContextValues(String paramString)
  {
    return this.exceptionContext.getContextValues(paramString);
  }
  
  public Object getFirstContextValue(String paramString)
  {
    return this.exceptionContext.getFirstContextValue(paramString);
  }
  
  public String getFormattedExceptionMessage(String paramString)
  {
    return this.exceptionContext.getFormattedExceptionMessage(paramString);
  }
  
  public String getMessage()
  {
    return getFormattedExceptionMessage(super.getMessage());
  }
  
  public String getRawMessage()
  {
    return super.getMessage();
  }
  
  public ContextedRuntimeException setContextValue(String paramString, Object paramObject)
  {
    this.exceptionContext.setContextValue(paramString, paramObject);
    return this;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/exception/ContextedRuntimeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */