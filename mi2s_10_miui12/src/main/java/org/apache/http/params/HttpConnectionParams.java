package org.apache.http.params;

@Deprecated
public final class HttpConnectionParams
  implements CoreConnectionPNames
{
  public static int getConnectionTimeout(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getIntParameter("http.connection.timeout", 0);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static int getLinger(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getIntParameter("http.socket.linger", -1);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static int getSoTimeout(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getIntParameter("http.socket.timeout", 0);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static int getSocketBufferSize(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getIntParameter("http.socket.buffer-size", -1);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static boolean getTcpNoDelay(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getBooleanParameter("http.tcp.nodelay", true);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static boolean isStaleCheckingEnabled(HttpParams paramHttpParams)
  {
    if (paramHttpParams != null) {
      return paramHttpParams.getBooleanParameter("http.connection.stalecheck", true);
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setConnectionTimeout(HttpParams paramHttpParams, int paramInt)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setIntParameter("http.connection.timeout", paramInt);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setLinger(HttpParams paramHttpParams, int paramInt)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setIntParameter("http.socket.linger", paramInt);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setSoTimeout(HttpParams paramHttpParams, int paramInt)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setIntParameter("http.socket.timeout", paramInt);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setSocketBufferSize(HttpParams paramHttpParams, int paramInt)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setIntParameter("http.socket.buffer-size", paramInt);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setStaleCheckingEnabled(HttpParams paramHttpParams, boolean paramBoolean)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setBooleanParameter("http.connection.stalecheck", paramBoolean);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
  
  public static void setTcpNoDelay(HttpParams paramHttpParams, boolean paramBoolean)
  {
    if (paramHttpParams != null)
    {
      paramHttpParams.setBooleanParameter("http.tcp.nodelay", paramBoolean);
      return;
    }
    throw new IllegalArgumentException("HTTP parameters may not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/http/params/HttpConnectionParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */