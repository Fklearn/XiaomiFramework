package android.webkit;

public class WebMessage
{
  private String mData;
  private WebMessagePort[] mPorts;
  
  public WebMessage(String paramString)
  {
    this.mData = paramString;
  }
  
  public WebMessage(String paramString, WebMessagePort[] paramArrayOfWebMessagePort)
  {
    this.mData = paramString;
    this.mPorts = paramArrayOfWebMessagePort;
  }
  
  public String getData()
  {
    return this.mData;
  }
  
  public WebMessagePort[] getPorts()
  {
    return this.mPorts;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */