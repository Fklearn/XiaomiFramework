package miui.push;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class XMPPError
{
  private List<CommonPacketExtension> applicationExtensions = null;
  private int code;
  private String condition;
  private String message;
  private String reason;
  private String type;
  
  public XMPPError(int paramInt)
  {
    this.code = paramInt;
    this.message = null;
  }
  
  public XMPPError(int paramInt, String paramString)
  {
    this.code = paramInt;
    this.message = paramString;
  }
  
  public XMPPError(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, List<CommonPacketExtension> paramList)
  {
    this.code = paramInt;
    this.type = paramString1;
    this.reason = paramString2;
    this.condition = paramString3;
    this.message = paramString4;
    this.applicationExtensions = paramList;
  }
  
  public XMPPError(Bundle paramBundle)
  {
    this.code = paramBundle.getInt("ext_err_code");
    if (paramBundle.containsKey("ext_err_type")) {
      this.type = paramBundle.getString("ext_err_type");
    }
    this.condition = paramBundle.getString("ext_err_cond");
    this.reason = paramBundle.getString("ext_err_reason");
    this.message = paramBundle.getString("ext_err_msg");
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("ext_exts");
    if (arrayOfParcelable != null)
    {
      this.applicationExtensions = new ArrayList(arrayOfParcelable.length);
      int i = arrayOfParcelable.length;
      for (int j = 0; j < i; j++)
      {
        paramBundle = CommonPacketExtension.parseFromBundle((Bundle)arrayOfParcelable[j]);
        if (paramBundle != null) {
          this.applicationExtensions.add(paramBundle);
        }
      }
    }
  }
  
  public XMPPError(Condition paramCondition)
  {
    init(paramCondition);
    this.message = null;
  }
  
  public XMPPError(Condition paramCondition, String paramString)
  {
    init(paramCondition);
    this.message = paramString;
  }
  
  private void init(Condition paramCondition)
  {
    this.condition = paramCondition.value;
  }
  
  public void addExtension(CommonPacketExtension paramCommonPacketExtension)
  {
    try
    {
      if (this.applicationExtensions == null)
      {
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>();
        this.applicationExtensions = localArrayList;
      }
      this.applicationExtensions.add(paramCommonPacketExtension);
      return;
    }
    finally {}
  }
  
  public int getCode()
  {
    return this.code;
  }
  
  public String getCondition()
  {
    return this.condition;
  }
  
  public PacketExtension getExtension(String paramString1, String paramString2)
  {
    try
    {
      if ((this.applicationExtensions != null) && (paramString1 != null) && (paramString2 != null))
      {
        Iterator localIterator = this.applicationExtensions.iterator();
        while (localIterator.hasNext())
        {
          PacketExtension localPacketExtension = (PacketExtension)localIterator.next();
          if (paramString1.equals(localPacketExtension.getElementName()))
          {
            boolean bool = paramString2.equals(localPacketExtension.getNamespace());
            if (bool) {
              return localPacketExtension;
            }
          }
        }
        return null;
      }
      return null;
    }
    finally {}
  }
  
  public List<CommonPacketExtension> getExtensions()
  {
    try
    {
      if (this.applicationExtensions == null)
      {
        localList = Collections.emptyList();
        return localList;
      }
      List localList = Collections.unmodifiableList(this.applicationExtensions);
      return localList;
    }
    finally {}
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public String getReason()
  {
    return this.reason;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setExtension(List<CommonPacketExtension> paramList)
  {
    try
    {
      this.applicationExtensions = paramList;
      return;
    }
    finally
    {
      paramList = finally;
      throw paramList;
    }
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    Object localObject = this.type;
    if (localObject != null) {
      localBundle.putString("ext_err_type", (String)localObject);
    }
    localBundle.putInt("ext_err_code", this.code);
    localObject = this.reason;
    if (localObject != null) {
      localBundle.putString("ext_err_reason", (String)localObject);
    }
    localObject = this.condition;
    if (localObject != null) {
      localBundle.putString("ext_err_cond", (String)localObject);
    }
    localObject = this.message;
    if (localObject != null) {
      localBundle.putString("ext_err_msg", (String)localObject);
    }
    localObject = this.applicationExtensions;
    if (localObject != null)
    {
      Bundle[] arrayOfBundle = new Bundle[((List)localObject).size()];
      int i = 0;
      Iterator localIterator = this.applicationExtensions.iterator();
      while (localIterator.hasNext())
      {
        localObject = ((CommonPacketExtension)localIterator.next()).toBundle();
        int j = i;
        if (localObject != null)
        {
          arrayOfBundle[i] = localObject;
          j = i + 1;
        }
        i = j;
      }
      localBundle.putParcelableArray("ext_exts", arrayOfBundle);
    }
    return localBundle;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = this.condition;
    if (str != null) {
      localStringBuilder.append(str);
    }
    localStringBuilder.append("(");
    localStringBuilder.append(this.code);
    localStringBuilder.append(")");
    if (this.message != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(this.message);
    }
    return localStringBuilder.toString();
  }
  
  public String toXML()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<error code=\"");
    localStringBuilder.append(this.code);
    localStringBuilder.append("\"");
    if (this.type != null)
    {
      localStringBuilder.append(" type=\"");
      localStringBuilder.append(this.type);
      localStringBuilder.append("\"");
    }
    if (this.reason != null)
    {
      localStringBuilder.append(" reason=\"");
      localStringBuilder.append(this.reason);
      localStringBuilder.append("\"");
    }
    localStringBuilder.append(">");
    if (this.condition != null)
    {
      localStringBuilder.append("<");
      localStringBuilder.append(this.condition);
      localStringBuilder.append(" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>");
    }
    if (this.message != null)
    {
      localStringBuilder.append("<text xml:lang=\"en\" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">");
      localStringBuilder.append(this.message);
      localStringBuilder.append("</text>");
    }
    Iterator localIterator = getExtensions().iterator();
    while (localIterator.hasNext()) {
      localStringBuilder.append(((PacketExtension)localIterator.next()).toXML());
    }
    localStringBuilder.append("</error>");
    return localStringBuilder.toString();
  }
  
  public static class Condition
  {
    public static final Condition bad_request;
    public static final Condition conflict;
    public static final Condition feature_not_implemented;
    public static final Condition forbidden;
    public static final Condition gone;
    public static final Condition interna_server_error = new Condition("internal-server-error");
    public static final Condition item_not_found;
    public static final Condition jid_malformed;
    public static final Condition no_acceptable;
    public static final Condition not_allowed;
    public static final Condition not_authorized;
    public static final Condition payment_required;
    public static final Condition recipient_unavailable;
    public static final Condition redirect;
    public static final Condition registration_required;
    public static final Condition remote_server_error;
    public static final Condition remote_server_not_found;
    public static final Condition remote_server_timeout;
    public static final Condition request_timeout = new Condition("request-timeout");
    public static final Condition resource_constraint;
    public static final Condition service_unavailable;
    public static final Condition subscription_required;
    public static final Condition undefined_condition;
    public static final Condition unexpected_request;
    private String value;
    
    static
    {
      forbidden = new Condition("forbidden");
      bad_request = new Condition("bad-request");
      conflict = new Condition("conflict");
      feature_not_implemented = new Condition("feature-not-implemented");
      gone = new Condition("gone");
      item_not_found = new Condition("item-not-found");
      jid_malformed = new Condition("jid-malformed");
      no_acceptable = new Condition("not-acceptable");
      not_allowed = new Condition("not-allowed");
      not_authorized = new Condition("not-authorized");
      payment_required = new Condition("payment-required");
      recipient_unavailable = new Condition("recipient-unavailable");
      redirect = new Condition("redirect");
      registration_required = new Condition("registration-required");
      remote_server_error = new Condition("remote-server-error");
      remote_server_not_found = new Condition("remote-server-not-found");
      remote_server_timeout = new Condition("remote-server-timeout");
      resource_constraint = new Condition("resource-constraint");
      service_unavailable = new Condition("service-unavailable");
      subscription_required = new Condition("subscription-required");
      undefined_condition = new Condition("undefined-condition");
      unexpected_request = new Condition("unexpected-request");
    }
    
    public Condition(String paramString)
    {
      this.value = paramString;
    }
    
    public String toString()
    {
      return this.value;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/XMPPError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */