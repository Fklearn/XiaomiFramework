package miui.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Slog;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;

public class SystemAnalytics
{
  public static final String CONFIGKEY_BOOT_SHUT = "systemserver_bootshuttime";
  private static final int LOGTYPE_EVENT = 0;
  private static final String SYSTEM_APP_ID = "systemserver";
  private static final String TAG = "SystemAnalytics";
  
  public static void trackSystem(Context paramContext, String paramString, Action paramAction)
  {
    try
    {
      Intent localIntent = new android/content/Intent;
      localIntent.<init>();
      localIntent.setClassName("com.miui.analytics", "com.miui.analytics.EventService");
      if (paramString == null) {
        paramString = "";
      }
      localIntent.putExtra("key", paramString);
      localIntent.putExtra("content", paramAction.getContent().toString());
      Slog.i("SystemAnalytics", paramAction.getContent().toString());
      localIntent.putExtra("extra", paramAction.getExtra().toString());
      localIntent.putExtra("appid", "systemserver");
      localIntent.putExtra("type", 0);
      paramContext.startService(localIntent);
    }
    catch (Exception paramContext)
    {
      Slog.e("SystemAnalytics", "track system error!", paramContext);
    }
  }
  
  public static class Action
  {
    protected static final String ACTION = "_action_";
    protected static final String CATEGORY = "_category_";
    protected static final String EVENT_ID = "_event_id_";
    protected static final String LABEL = "_label_";
    protected static final String VALUE = "_value_";
    private JSONObject mContent = new JSONObject();
    private JSONObject mExtra = new JSONObject();
    private Set<String> sKeywords = new HashSet();
    
    public Action()
    {
      this.sKeywords.add("_event_id_");
      this.sKeywords.add("_category_");
      this.sKeywords.add("_action_");
      this.sKeywords.add("_label_");
      this.sKeywords.add("_value_");
    }
    
    private void ensureKey(String paramString)
    {
      if ((!TextUtils.isEmpty(paramString)) && (this.sKeywords.contains(paramString)))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("this key ");
        localStringBuilder.append(paramString);
        localStringBuilder.append(" is built-in, please pick another key.");
        throw new IllegalArgumentException(localStringBuilder.toString());
      }
    }
    
    void addContent(String paramString, int paramInt)
    {
      if (!TextUtils.isEmpty(paramString)) {
        try
        {
          this.mContent.put(paramString, paramInt);
        }
        catch (Exception paramString)
        {
          paramString.printStackTrace();
        }
      }
    }
    
    void addContent(String paramString, long paramLong)
    {
      if (!TextUtils.isEmpty(paramString)) {
        try
        {
          this.mContent.put(paramString, paramLong);
        }
        catch (Exception paramString)
        {
          paramString.printStackTrace();
        }
      }
    }
    
    void addContent(String paramString, Object paramObject)
    {
      if (!TextUtils.isEmpty(paramString)) {
        try
        {
          this.mContent.put(paramString, paramObject);
        }
        catch (Exception paramString)
        {
          paramString.printStackTrace();
        }
      }
    }
    
    void addContent(JSONObject paramJSONObject)
    {
      if (paramJSONObject != null)
      {
        Iterator localIterator = paramJSONObject.keys();
        while (localIterator.hasNext())
        {
          String str = localIterator.next().toString();
          ensureKey(str);
          try
          {
            this.mContent.put(str, paramJSONObject.get(str));
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
          }
        }
      }
    }
    
    protected Action addEventId(String paramString)
    {
      addContent("_event_id_", paramString);
      return this;
    }
    
    void addExtra(String paramString1, String paramString2)
    {
      try
      {
        this.mExtra.put(paramString1, paramString2);
      }
      catch (Exception paramString1)
      {
        paramString1.printStackTrace();
      }
    }
    
    public Action addParam(String paramString, int paramInt)
    {
      ensureKey(paramString);
      addContent(paramString, paramInt);
      return this;
    }
    
    public Action addParam(String paramString, long paramLong)
    {
      ensureKey(paramString);
      addContent(paramString, paramLong);
      return this;
    }
    
    public Action addParam(String paramString1, String paramString2)
    {
      ensureKey(paramString1);
      addContent(paramString1, paramString2);
      return this;
    }
    
    public Action addParam(String paramString, JSONObject paramJSONObject)
    {
      ensureKey(paramString);
      addContent(paramString, paramJSONObject);
      return this;
    }
    
    final JSONObject getContent()
    {
      return this.mContent;
    }
    
    final JSONObject getExtra()
    {
      return this.mExtra;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/SystemAnalytics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */