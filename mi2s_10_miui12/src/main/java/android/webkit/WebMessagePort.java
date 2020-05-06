package android.webkit;

import android.annotation.SystemApi;
import android.os.Handler;

public abstract class WebMessagePort
{
  public abstract void close();
  
  public abstract void postMessage(WebMessage paramWebMessage);
  
  public abstract void setWebMessageCallback(WebMessageCallback paramWebMessageCallback);
  
  public abstract void setWebMessageCallback(WebMessageCallback paramWebMessageCallback, Handler paramHandler);
  
  public static abstract class WebMessageCallback
  {
    public void onMessage(WebMessagePort paramWebMessagePort, WebMessage paramWebMessage) {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebMessagePort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */