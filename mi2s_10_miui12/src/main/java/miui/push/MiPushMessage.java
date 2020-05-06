package miui.push;

import android.os.Bundle;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class MiPushMessage
{
  private static final String KEY_ALIAS = "alias";
  private static final String KEY_CATEGORY = "category";
  private static final String KEY_CONTENT = "content";
  private static final String KEY_DESC = "description";
  private static final String KEY_EXTRA = "extra";
  private static final String KEY_MESSAGE_ID = "messageId";
  private static final String KEY_MESSAGE_TYPE = "messageType";
  private static final String KEY_NOTIFIED = "isNotified";
  private static final String KEY_NOTIFY_ID = "notifyId";
  private static final String KEY_NOTIFY_TYPE = "notifyType";
  private static final String KEY_PASS_THROUGH = "passThrough";
  private static final String KEY_TITLE = "title";
  private static final String KEY_TOPIC = "topic";
  public static final int MESSAGE_TYPE_ALIAS = 1;
  public static final int MESSAGE_TYPE_REG = 0;
  public static final int MESSAGE_TYPE_TOPIC = 2;
  private static final long serialVersionUID = 1L;
  private String alias;
  private String category;
  private String content;
  private String description;
  private HashMap<String, String> extra = new HashMap();
  private boolean isNotified;
  private String messageId;
  private int messageType;
  private int notifyId;
  private int notifyType;
  private int passThrough;
  private String title;
  private String topic;
  
  public static MiPushMessage fromBundle(Bundle paramBundle)
  {
    MiPushMessage localMiPushMessage = new MiPushMessage();
    localMiPushMessage.messageId = paramBundle.getString("messageId");
    localMiPushMessage.messageType = paramBundle.getInt("messageType");
    localMiPushMessage.passThrough = paramBundle.getInt("passThrough");
    localMiPushMessage.alias = paramBundle.getString("alias");
    localMiPushMessage.topic = paramBundle.getString("topic");
    localMiPushMessage.content = paramBundle.getString("content");
    localMiPushMessage.description = paramBundle.getString("description");
    localMiPushMessage.title = paramBundle.getString("title");
    localMiPushMessage.isNotified = paramBundle.getBoolean("isNotified");
    localMiPushMessage.notifyId = paramBundle.getInt("notifyId");
    localMiPushMessage.notifyType = paramBundle.getInt("notifyType");
    localMiPushMessage.category = paramBundle.getString("category");
    localMiPushMessage.extra = ((HashMap)paramBundle.getSerializable("extra"));
    return localMiPushMessage;
  }
  
  public String getAlias()
  {
    return this.alias;
  }
  
  public String getCategory()
  {
    return this.category;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public Map<String, String> getExtra()
  {
    return this.extra;
  }
  
  public String getMessageId()
  {
    return this.messageId;
  }
  
  public int getMessageType()
  {
    return this.messageType;
  }
  
  public int getNotifyId()
  {
    return this.notifyId;
  }
  
  public int getNotifyType()
  {
    return this.notifyType;
  }
  
  public int getPassThrough()
  {
    return this.passThrough;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public String getTopic()
  {
    return this.topic;
  }
  
  public boolean isNotified()
  {
    return this.isNotified;
  }
  
  public void setAlias(String paramString)
  {
    this.alias = paramString;
  }
  
  public void setCategory(String paramString)
  {
    this.category = paramString;
  }
  
  public void setContent(String paramString)
  {
    this.content = paramString;
  }
  
  public void setDescription(String paramString)
  {
    this.description = paramString;
  }
  
  public void setExtra(Map<String, String> paramMap)
  {
    this.extra.clear();
    if (paramMap != null) {
      this.extra.putAll(paramMap);
    }
  }
  
  public void setMessageId(String paramString)
  {
    this.messageId = paramString;
  }
  
  public void setMessageType(int paramInt)
  {
    this.messageType = paramInt;
  }
  
  public void setNotified(boolean paramBoolean)
  {
    this.isNotified = paramBoolean;
  }
  
  public void setNotifyId(int paramInt)
  {
    this.notifyId = paramInt;
  }
  
  public void setNotifyType(int paramInt)
  {
    this.notifyType = paramInt;
  }
  
  public void setPassThrough(int paramInt)
  {
    this.passThrough = paramInt;
  }
  
  public void setTitle(String paramString)
  {
    this.title = paramString;
  }
  
  public void setTopic(String paramString)
  {
    this.topic = paramString;
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("messageId", this.messageId);
    localBundle.putInt("passThrough", this.passThrough);
    localBundle.putInt("messageType", this.messageType);
    if (!TextUtils.isEmpty(this.alias)) {
      localBundle.putString("alias", this.alias);
    }
    if (!TextUtils.isEmpty(this.topic)) {
      localBundle.putString("topic", this.topic);
    }
    localBundle.putString("content", this.content);
    if (!TextUtils.isEmpty(this.description)) {
      localBundle.putString("description", this.description);
    }
    if (!TextUtils.isEmpty(this.title)) {
      localBundle.putString("title", this.title);
    }
    localBundle.putBoolean("isNotified", this.isNotified);
    localBundle.putInt("notifyId", this.notifyId);
    localBundle.putInt("notifyType", this.notifyType);
    if (!TextUtils.isEmpty(this.category)) {
      localBundle.putString("category", this.category);
    }
    HashMap localHashMap = this.extra;
    if (localHashMap != null) {
      localBundle.putSerializable("extra", localHashMap);
    }
    return localBundle;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("messageId={");
    localStringBuilder.append(this.messageId);
    localStringBuilder.append("},passThrough={");
    localStringBuilder.append(this.passThrough);
    localStringBuilder.append("},alias={");
    localStringBuilder.append(this.alias);
    localStringBuilder.append("},topic={");
    localStringBuilder.append(this.topic);
    localStringBuilder.append("},content={");
    localStringBuilder.append(this.content);
    localStringBuilder.append("},description={");
    localStringBuilder.append(this.description);
    localStringBuilder.append("},title={");
    localStringBuilder.append(this.title);
    localStringBuilder.append("},isNotified={");
    localStringBuilder.append(this.isNotified);
    localStringBuilder.append("},notifyId={");
    localStringBuilder.append(this.notifyId);
    localStringBuilder.append("},notifyType={");
    localStringBuilder.append(this.notifyType);
    localStringBuilder.append("}, category={");
    localStringBuilder.append(this.category);
    localStringBuilder.append("}, extra={");
    localStringBuilder.append(this.extra);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/MiPushMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */