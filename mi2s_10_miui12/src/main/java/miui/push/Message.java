package miui.push;

import android.os.Bundle;
import android.text.TextUtils;

public class Message
  extends Packet
{
  public static final String MSG_TYPE_CHAT = "chat";
  public static final String MSG_TYPE_ERROR = "error";
  public static final String MSG_TYPE_GROUPCHAT = "groupchat";
  public static final String MSG_TYPE_HEADLINE = "hearline";
  public static final String MSG_TYPE_NORMAL = "normal";
  public static final String MSG_TYPE_PPL = "ppl";
  private String fseq = "";
  private String language;
  private String mAppId;
  private String mBody;
  private String mBodyEncoding;
  private boolean mEncrypted = false;
  private String mSubject;
  private boolean mTransient = false;
  private String mseq = "";
  private String seq = "";
  private String status = "";
  private String thread = null;
  private String type = null;
  
  public Message() {}
  
  public Message(Bundle paramBundle)
  {
    super(paramBundle);
    this.type = paramBundle.getString("ext_msg_type");
    this.language = paramBundle.getString("ext_msg_lang");
    this.thread = paramBundle.getString("ext_msg_thread");
    this.mSubject = paramBundle.getString("ext_msg_sub");
    this.mBody = paramBundle.getString("ext_msg_body");
    this.mBodyEncoding = paramBundle.getString("ext_body_encode");
    this.mAppId = paramBundle.getString("ext_msg_appid");
    this.mTransient = paramBundle.getBoolean("ext_msg_trans", false);
    this.seq = paramBundle.getString("ext_msg_seq");
    this.mseq = paramBundle.getString("ext_msg_mseq");
    this.fseq = paramBundle.getString("ext_msg_fseq");
    this.status = paramBundle.getString("ext_msg_status");
  }
  
  public Message(String paramString)
  {
    setTo(paramString);
  }
  
  public Message(String paramString1, String paramString2)
  {
    setTo(paramString1);
    this.type = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (Message)paramObject;
      if (!super.equals(paramObject)) {
        return false;
      }
      String str = this.mBody;
      if (str != null ? !str.equals(((Message)paramObject).mBody) : ((Message)paramObject).mBody != null) {
        return false;
      }
      str = this.language;
      if (str != null ? !str.equals(((Message)paramObject).language) : ((Message)paramObject).language != null) {
        return false;
      }
      str = this.mSubject;
      if (str != null ? !str.equals(((Message)paramObject).mSubject) : ((Message)paramObject).mSubject != null) {
        return false;
      }
      str = this.thread;
      if (str != null ? !str.equals(((Message)paramObject).thread) : ((Message)paramObject).thread != null) {
        return false;
      }
      if (this.type != ((Message)paramObject).type) {
        bool = false;
      }
      return bool;
    }
    return false;
  }
  
  public String getAppId()
  {
    return this.mAppId;
  }
  
  public String getBody()
  {
    return this.mBody;
  }
  
  public String getBodyEncoding()
  {
    return this.mBodyEncoding;
  }
  
  public boolean getEncrypted()
  {
    return this.mEncrypted;
  }
  
  public String getFSeq()
  {
    return this.fseq;
  }
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public String getMSeq()
  {
    return this.mseq;
  }
  
  public String getSeq()
  {
    return this.seq;
  }
  
  public String getStatus()
  {
    return this.status;
  }
  
  public String getSubject()
  {
    return this.mSubject;
  }
  
  public String getThread()
  {
    return this.thread;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public int hashCode()
  {
    String str = this.type;
    int i = 0;
    int j;
    if (str != null) {
      j = str.hashCode();
    } else {
      j = 0;
    }
    str = this.mBody;
    int k;
    if (str != null) {
      k = str.hashCode();
    } else {
      k = 0;
    }
    str = this.thread;
    int m;
    if (str != null) {
      m = str.hashCode();
    } else {
      m = 0;
    }
    str = this.language;
    int n;
    if (str != null) {
      n = str.hashCode();
    } else {
      n = 0;
    }
    str = this.mSubject;
    if (str != null) {
      i = str.hashCode();
    }
    return (((j * 31 + k) * 31 + m) * 31 + n) * 31 + i;
  }
  
  public void setAppId(String paramString)
  {
    this.mAppId = paramString;
  }
  
  public void setBody(String paramString)
  {
    this.mBody = paramString;
  }
  
  public void setBody(String paramString1, String paramString2)
  {
    this.mBody = paramString1;
    this.mBodyEncoding = paramString2;
  }
  
  public void setEncrypted(boolean paramBoolean)
  {
    this.mEncrypted = paramBoolean;
  }
  
  public void setFSeq(String paramString)
  {
    this.fseq = paramString;
  }
  
  public void setIsTransient(boolean paramBoolean)
  {
    this.mTransient = paramBoolean;
  }
  
  public void setLanguage(String paramString)
  {
    this.language = paramString;
  }
  
  public void setMSeq(String paramString)
  {
    this.mseq = paramString;
  }
  
  public void setSeq(String paramString)
  {
    this.seq = paramString;
  }
  
  public void setStatus(String paramString)
  {
    this.status = paramString;
  }
  
  public void setSubject(String paramString)
  {
    this.mSubject = paramString;
  }
  
  public void setThread(String paramString)
  {
    this.thread = paramString;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = super.toBundle();
    if (!TextUtils.isEmpty(this.type)) {
      localBundle.putString("ext_msg_type", this.type);
    }
    String str = this.language;
    if (str != null) {
      localBundle.putString("ext_msg_lang", str);
    }
    str = this.mSubject;
    if (str != null) {
      localBundle.putString("ext_msg_sub", str);
    }
    str = this.mBody;
    if (str != null) {
      localBundle.putString("ext_msg_body", str);
    }
    if (!TextUtils.isEmpty(this.mBodyEncoding)) {
      localBundle.putString("ext_body_encode", this.mBodyEncoding);
    }
    str = this.thread;
    if (str != null) {
      localBundle.putString("ext_msg_thread", str);
    }
    str = this.mAppId;
    if (str != null) {
      localBundle.putString("ext_msg_appid", str);
    }
    if (this.mTransient) {
      localBundle.putBoolean("ext_msg_trans", true);
    }
    if (!TextUtils.isEmpty(this.seq)) {
      localBundle.putString("ext_msg_seq", this.seq);
    }
    if (!TextUtils.isEmpty(this.mseq)) {
      localBundle.putString("ext_msg_mseq", this.mseq);
    }
    if (!TextUtils.isEmpty(this.fseq)) {
      localBundle.putString("ext_msg_fseq", this.fseq);
    }
    if (!TextUtils.isEmpty(this.status)) {
      localBundle.putString("ext_msg_status", this.status);
    }
    return localBundle;
  }
  
  public String toXML()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<message");
    if (getXmlns() != null)
    {
      localStringBuilder.append(" xmlns=\"");
      localStringBuilder.append(getXmlns());
      localStringBuilder.append("\"");
    }
    if (this.language != null)
    {
      localStringBuilder.append(" xml:lang=\"");
      localStringBuilder.append(getLanguage());
      localStringBuilder.append("\"");
    }
    if (getPacketID() != null)
    {
      localStringBuilder.append(" id=\"");
      localStringBuilder.append(getPacketID());
      localStringBuilder.append("\"");
    }
    if (getTo() != null)
    {
      localStringBuilder.append(" to=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getTo()));
      localStringBuilder.append("\"");
    }
    if (!TextUtils.isEmpty(getSeq()))
    {
      localStringBuilder.append(" seq=\"");
      localStringBuilder.append(getSeq());
      localStringBuilder.append("\"");
    }
    if (!TextUtils.isEmpty(getMSeq()))
    {
      localStringBuilder.append(" mseq=\"");
      localStringBuilder.append(getMSeq());
      localStringBuilder.append("\"");
    }
    if (!TextUtils.isEmpty(getFSeq()))
    {
      localStringBuilder.append(" fseq=\"");
      localStringBuilder.append(getFSeq());
      localStringBuilder.append("\"");
    }
    if (!TextUtils.isEmpty(getStatus()))
    {
      localStringBuilder.append(" status=\"");
      localStringBuilder.append(getStatus());
      localStringBuilder.append("\"");
    }
    if (getFrom() != null)
    {
      localStringBuilder.append(" from=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getFrom()));
      localStringBuilder.append("\"");
    }
    if (getChannelId() != null)
    {
      localStringBuilder.append(" chid=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getChannelId()));
      localStringBuilder.append("\"");
    }
    if (this.mTransient) {
      localStringBuilder.append(" transient=\"true\"");
    }
    if (!TextUtils.isEmpty(this.mAppId))
    {
      localStringBuilder.append(" appid=\"");
      localStringBuilder.append(getAppId());
      localStringBuilder.append("\"");
    }
    if (!TextUtils.isEmpty(this.type))
    {
      localStringBuilder.append(" type=\"");
      localStringBuilder.append(this.type);
      localStringBuilder.append("\"");
    }
    if (this.mEncrypted) {
      localStringBuilder.append(" s=\"1\"");
    }
    localStringBuilder.append(">");
    if (this.mSubject != null)
    {
      localStringBuilder.append("<subject>");
      localStringBuilder.append(StringUtils.escapeForXML(this.mSubject));
      localStringBuilder.append("</subject>");
    }
    if (this.mBody != null)
    {
      localStringBuilder.append("<body");
      if (!TextUtils.isEmpty(this.mBodyEncoding))
      {
        localStringBuilder.append(" encode=\"");
        localStringBuilder.append(this.mBodyEncoding);
        localStringBuilder.append("\"");
      }
      localStringBuilder.append(">");
      localStringBuilder.append(StringUtils.escapeForXML(this.mBody));
      localStringBuilder.append("</body>");
    }
    if (this.thread != null)
    {
      localStringBuilder.append("<thread>");
      localStringBuilder.append(this.thread);
      localStringBuilder.append("</thread>");
    }
    if ("error".equalsIgnoreCase(this.type))
    {
      XMPPError localXMPPError = getError();
      if (localXMPPError != null) {
        localStringBuilder.append(localXMPPError.toXML());
      }
    }
    localStringBuilder.append(getExtensionsXML());
    localStringBuilder.append("</message>");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/Message.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */