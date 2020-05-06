package android.webkit;

import android.annotation.SystemApi;
import android.graphics.Bitmap;

public abstract class WebHistoryItem
  implements Cloneable
{
  protected abstract WebHistoryItem clone();
  
  public abstract Bitmap getFavicon();
  
  @SystemApi
  @Deprecated
  public abstract int getId();
  
  public abstract String getOriginalUrl();
  
  public abstract String getTitle();
  
  public abstract String getUrl();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebHistoryItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */