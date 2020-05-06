package miui.push;

import android.text.TextUtils;
import com.google.android.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PushAttributes
{
  private final Map<String, String> mAttrs = Maps.newHashMap();
  
  public PushAttributes(Map<String, String> paramMap)
  {
    this.mAttrs.putAll(paramMap);
  }
  
  public static PushAttributes parse(String paramString)
  {
    HashMap localHashMap = Maps.newHashMap();
    if (!TextUtils.isEmpty(paramString))
    {
      String[] arrayOfString = paramString.split(",");
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        paramString = arrayOfString[j].split(":");
        if (paramString.length == 2) {
          localHashMap.put(paramString[0], paramString[1]);
        }
      }
    }
    return new PushAttributes(localHashMap);
  }
  
  public String get(String paramString)
  {
    return (String)this.mAttrs.get(paramString);
  }
  
  public String toPlain()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mAttrs.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append(':');
      localStringBuilder.append((String)localEntry.getValue());
    }
    return localStringBuilder.toString();
  }
  
  public String toString()
  {
    return toPlain();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/PushAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */