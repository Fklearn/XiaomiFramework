package org.apache.http.conn;

import java.io.InterruptedIOException;

@Deprecated
public class ConnectTimeoutException
  extends InterruptedIOException
{
  private static final long serialVersionUID = -4816682903149535989L;
  
  public ConnectTimeoutException() {}
  
  public ConnectTimeoutException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/http/conn/ConnectTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */