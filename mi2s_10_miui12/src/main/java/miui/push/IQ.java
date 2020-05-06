package miui.push;

import android.os.Bundle;

public class IQ
  extends Packet
{
  private Type type = Type.GET;
  
  public IQ() {}
  
  public IQ(Bundle paramBundle)
  {
    super(paramBundle);
    if (paramBundle.containsKey("ext_iq_type")) {
      this.type = Type.fromString(paramBundle.getString("ext_iq_type"));
    }
  }
  
  public static IQ createErrorResponse(IQ paramIQ, XMPPError paramXMPPError)
  {
    if ((paramIQ.getType() != Type.GET) && (paramIQ.getType() != Type.SET))
    {
      paramXMPPError = new StringBuilder();
      paramXMPPError.append("IQ must be of type 'set' or 'get'. Original IQ: ");
      paramXMPPError.append(paramIQ.toXML());
      throw new IllegalArgumentException(paramXMPPError.toString());
    }
    IQ local2 = new IQ()
    {
      public String getChildElementXML()
      {
        return IQ.this.getChildElementXML();
      }
    };
    local2.setType(Type.ERROR);
    local2.setPacketID(paramIQ.getPacketID());
    local2.setFrom(paramIQ.getTo());
    local2.setTo(paramIQ.getFrom());
    local2.setError(paramXMPPError);
    return local2;
  }
  
  public static IQ createResultIQ(IQ paramIQ)
  {
    if ((paramIQ.getType() != Type.GET) && (paramIQ.getType() != Type.SET))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("IQ must be of type 'set' or 'get'. Original IQ: ");
      ((StringBuilder)localObject).append(paramIQ.toXML());
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    Object localObject = new IQ()
    {
      public String getChildElementXML()
      {
        return null;
      }
    };
    ((IQ)localObject).setType(Type.RESULT);
    ((IQ)localObject).setPacketID(paramIQ.getPacketID());
    ((IQ)localObject).setFrom(paramIQ.getTo());
    ((IQ)localObject).setTo(paramIQ.getFrom());
    return (IQ)localObject;
  }
  
  public String getChildElementXML()
  {
    return null;
  }
  
  public Type getType()
  {
    return this.type;
  }
  
  public void setType(Type paramType)
  {
    if (paramType == null) {
      this.type = Type.GET;
    } else {
      this.type = paramType;
    }
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = super.toBundle();
    Type localType = this.type;
    if (localType != null) {
      localBundle.putString("ext_iq_type", localType.toString());
    }
    return localBundle;
  }
  
  public String toXML()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<iq ");
    if (getPacketID() != null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("id=\"");
      ((StringBuilder)localObject).append(getPacketID());
      ((StringBuilder)localObject).append("\" ");
      localStringBuilder.append(((StringBuilder)localObject).toString());
    }
    if (getTo() != null)
    {
      localStringBuilder.append("to=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getTo()));
      localStringBuilder.append("\" ");
    }
    if (getFrom() != null)
    {
      localStringBuilder.append("from=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getFrom()));
      localStringBuilder.append("\" ");
    }
    if (getChannelId() != null)
    {
      localStringBuilder.append("chid=\"");
      localStringBuilder.append(StringUtils.escapeForXML(getChannelId()));
      localStringBuilder.append("\" ");
    }
    if (this.type == null)
    {
      localStringBuilder.append("type=\"get\">");
    }
    else
    {
      localStringBuilder.append("type=\"");
      localStringBuilder.append(getType());
      localStringBuilder.append("\">");
    }
    Object localObject = getChildElementXML();
    if (localObject != null) {
      localStringBuilder.append((String)localObject);
    }
    localStringBuilder.append(getExtensionsXML());
    localObject = getError();
    if (localObject != null) {
      localStringBuilder.append(((XMPPError)localObject).toXML());
    }
    localStringBuilder.append("</iq>");
    return localStringBuilder.toString();
  }
  
  public static class Type
  {
    public static final Type ERROR = new Type("error");
    public static final Type GET = new Type("get");
    public static final Type RESULT;
    public static final Type SET = new Type("set");
    private String value;
    
    static
    {
      RESULT = new Type("result");
    }
    
    private Type(String paramString)
    {
      this.value = paramString;
    }
    
    public static Type fromString(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      paramString = paramString.toLowerCase();
      if (GET.toString().equals(paramString)) {
        return GET;
      }
      if (SET.toString().equals(paramString)) {
        return SET;
      }
      if (ERROR.toString().equals(paramString)) {
        return ERROR;
      }
      if (RESULT.toString().equals(paramString)) {
        return RESULT;
      }
      return null;
    }
    
    public String toString()
    {
      return this.value;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/IQ.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */