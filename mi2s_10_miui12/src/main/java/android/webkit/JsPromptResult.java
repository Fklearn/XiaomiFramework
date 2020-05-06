package android.webkit;

import android.annotation.SystemApi;

public class JsPromptResult
  extends JsResult
{
  private String mStringResult;
  
  @SystemApi
  public JsPromptResult(JsResult.ResultReceiver paramResultReceiver)
  {
    super(paramResultReceiver);
  }
  
  public void confirm(String paramString)
  {
    this.mStringResult = paramString;
    confirm();
  }
  
  @SystemApi
  public String getStringResult()
  {
    return this.mStringResult;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/JsPromptResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */