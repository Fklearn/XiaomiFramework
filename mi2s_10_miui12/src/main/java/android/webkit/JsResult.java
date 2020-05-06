package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;

public class JsResult
{
  @UnsupportedAppUsage
  private final ResultReceiver mReceiver;
  private boolean mResult;
  
  @SystemApi
  public JsResult(ResultReceiver paramResultReceiver)
  {
    this.mReceiver = paramResultReceiver;
  }
  
  private final void wakeUp()
  {
    this.mReceiver.onJsResultComplete(this);
  }
  
  public final void cancel()
  {
    this.mResult = false;
    wakeUp();
  }
  
  public final void confirm()
  {
    this.mResult = true;
    wakeUp();
  }
  
  @SystemApi
  public final boolean getResult()
  {
    return this.mResult;
  }
  
  @SystemApi
  public static abstract interface ResultReceiver
  {
    public abstract void onJsResultComplete(JsResult paramJsResult);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/JsResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */