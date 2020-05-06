package android.webkit;

import java.io.Serializable;

public abstract class WebBackForwardList
  implements Cloneable, Serializable
{
  protected abstract WebBackForwardList clone();
  
  public abstract int getCurrentIndex();
  
  public abstract WebHistoryItem getCurrentItem();
  
  public abstract WebHistoryItem getItemAtIndex(int paramInt);
  
  public abstract int getSize();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebBackForwardList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */