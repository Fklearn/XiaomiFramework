package android.webkit;

import java.io.OutputStream;
import java.util.concurrent.Executor;

public abstract class TracingController
{
  public static TracingController getInstance()
  {
    return WebViewFactory.getProvider().getTracingController();
  }
  
  public abstract boolean isTracing();
  
  public abstract void start(TracingConfig paramTracingConfig);
  
  public abstract boolean stop(OutputStream paramOutputStream, Executor paramExecutor);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/TracingController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */