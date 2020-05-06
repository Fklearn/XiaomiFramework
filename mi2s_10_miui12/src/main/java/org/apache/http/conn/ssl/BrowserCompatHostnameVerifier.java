package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;

@Deprecated
public class BrowserCompatHostnameVerifier
  extends AbstractVerifier
{
  public final String toString()
  {
    return "BROWSER_COMPATIBLE";
  }
  
  public final void verify(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws SSLException
  {
    verify(paramString, paramArrayOfString1, paramArrayOfString2, false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/http/conn/ssl/BrowserCompatHostnameVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */