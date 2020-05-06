package miui.push;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CommonPacketExtension
  implements PacketExtension
{
  public static final String ATTRIBUTE_NAME = "attributes";
  public static final String CHILDREN_NAME = "children";
  private String[] mAttributeNames = null;
  private String[] mAttributeValues = null;
  private List<CommonPacketExtension> mChildrenEles = null;
  private String mExtensionElementName;
  private String mNamespace;
  private String mText;
  
  public CommonPacketExtension(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.mExtensionElementName = paramString1;
    this.mNamespace = paramString2;
    this.mAttributeNames = new String[] { paramString3 };
    this.mAttributeValues = new String[] { paramString4 };
  }
  
  public CommonPacketExtension(String paramString1, String paramString2, List<String> paramList1, List<String> paramList2)
  {
    this.mExtensionElementName = paramString1;
    this.mNamespace = paramString2;
    this.mAttributeNames = ((String[])paramList1.toArray(new String[paramList1.size()]));
    this.mAttributeValues = ((String[])paramList2.toArray(new String[paramList2.size()]));
  }
  
  public CommonPacketExtension(String paramString1, String paramString2, List<String> paramList1, List<String> paramList2, String paramString3, List<CommonPacketExtension> paramList)
  {
    this.mExtensionElementName = paramString1;
    this.mNamespace = paramString2;
    this.mAttributeNames = ((String[])paramList1.toArray(new String[paramList1.size()]));
    this.mAttributeValues = ((String[])paramList2.toArray(new String[paramList2.size()]));
    this.mText = paramString3;
    this.mChildrenEles = paramList;
  }
  
  public CommonPacketExtension(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.mExtensionElementName = paramString1;
    this.mNamespace = paramString2;
    this.mAttributeNames = paramArrayOfString1;
    this.mAttributeValues = paramArrayOfString2;
  }
  
  public CommonPacketExtension(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String paramString3, List<CommonPacketExtension> paramList)
  {
    this.mExtensionElementName = paramString1;
    this.mNamespace = paramString2;
    this.mAttributeNames = paramArrayOfString1;
    this.mAttributeValues = paramArrayOfString2;
    this.mText = paramString3;
    this.mChildrenEles = paramList;
  }
  
  public static CommonPacketExtension[] getArray(Parcelable[] paramArrayOfParcelable)
  {
    int i;
    if (paramArrayOfParcelable == null) {
      i = 0;
    } else {
      i = paramArrayOfParcelable.length;
    }
    CommonPacketExtension[] arrayOfCommonPacketExtension = new CommonPacketExtension[i];
    if (paramArrayOfParcelable != null) {
      for (i = 0; i < paramArrayOfParcelable.length; i++) {
        arrayOfCommonPacketExtension[i] = parseFromBundle((Bundle)paramArrayOfParcelable[i]);
      }
    }
    return arrayOfCommonPacketExtension;
  }
  
  public static CommonPacketExtension parseFromBundle(Bundle paramBundle)
  {
    String str1 = paramBundle.getString("ext_ele_name");
    String str2 = paramBundle.getString("ext_ns");
    String str3 = paramBundle.getString("ext_text");
    Object localObject1 = paramBundle.getBundle("attributes");
    Object localObject2 = ((Bundle)localObject1).keySet();
    String[] arrayOfString1 = new String[((Set)localObject2).size()];
    String[] arrayOfString2 = new String[((Set)localObject2).size()];
    localObject2 = ((Set)localObject2).iterator();
    for (int i = 0; ((Iterator)localObject2).hasNext(); i++)
    {
      String str4 = (String)((Iterator)localObject2).next();
      arrayOfString1[i] = str4;
      arrayOfString2[i] = ((Bundle)localObject1).getString(str4);
    }
    if (paramBundle.containsKey("children"))
    {
      localObject1 = paramBundle.getParcelableArray("children");
      paramBundle = new ArrayList(localObject1.length);
      int j = localObject1.length;
      for (i = 0; i < j; i++) {
        paramBundle.add(parseFromBundle((Bundle)localObject1[i]));
      }
    }
    else
    {
      paramBundle = null;
    }
    return new CommonPacketExtension(str1, str2, arrayOfString1, arrayOfString2, str3, paramBundle);
  }
  
  public static Parcelable[] toParcelableArray(List<CommonPacketExtension> paramList)
  {
    return toParcelableArray((CommonPacketExtension[])paramList.toArray(new CommonPacketExtension[paramList.size()]));
  }
  
  public static Parcelable[] toParcelableArray(CommonPacketExtension[] paramArrayOfCommonPacketExtension)
  {
    if (paramArrayOfCommonPacketExtension == null) {
      return null;
    }
    Parcelable[] arrayOfParcelable = new Parcelable[paramArrayOfCommonPacketExtension.length];
    for (int i = 0; i < paramArrayOfCommonPacketExtension.length; i++) {
      arrayOfParcelable[i] = paramArrayOfCommonPacketExtension[i].toParcelable();
    }
    return arrayOfParcelable;
  }
  
  public void appendChild(CommonPacketExtension paramCommonPacketExtension)
  {
    if (this.mChildrenEles == null) {
      this.mChildrenEles = new ArrayList();
    }
    if (!this.mChildrenEles.contains(paramCommonPacketExtension)) {
      this.mChildrenEles.add(paramCommonPacketExtension);
    }
  }
  
  public String getAttributeValue(String paramString)
  {
    if (paramString != null)
    {
      if (this.mAttributeNames != null) {
        for (int i = 0;; i++)
        {
          String[] arrayOfString = this.mAttributeNames;
          if (i >= arrayOfString.length) {
            break;
          }
          if (paramString.equals(arrayOfString[i])) {
            return this.mAttributeValues[i];
          }
        }
      }
      return null;
    }
    throw new IllegalArgumentException();
  }
  
  public CommonPacketExtension getChildByName(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      Object localObject = this.mChildrenEles;
      if (localObject != null)
      {
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          CommonPacketExtension localCommonPacketExtension = (CommonPacketExtension)((Iterator)localObject).next();
          if (localCommonPacketExtension.mExtensionElementName.equals(paramString)) {
            return localCommonPacketExtension;
          }
        }
        return null;
      }
    }
    return null;
  }
  
  public List<CommonPacketExtension> getChildrenByName(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (this.mChildrenEles != null))
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mChildrenEles.iterator();
      while (localIterator.hasNext())
      {
        CommonPacketExtension localCommonPacketExtension = (CommonPacketExtension)localIterator.next();
        if (localCommonPacketExtension.mExtensionElementName.equals(paramString)) {
          localArrayList.add(localCommonPacketExtension);
        }
      }
      return localArrayList;
    }
    return null;
  }
  
  public List<CommonPacketExtension> getChildrenExt()
  {
    return this.mChildrenEles;
  }
  
  public String getElementName()
  {
    return this.mExtensionElementName;
  }
  
  public String getNamespace()
  {
    return this.mNamespace;
  }
  
  public String getText()
  {
    if (!TextUtils.isEmpty(this.mText)) {
      return StringUtils.unescapeFromXML(this.mText);
    }
    return this.mText;
  }
  
  public void setText(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {
      this.mText = StringUtils.escapeForXML(paramString);
    } else {
      this.mText = paramString;
    }
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("ext_ele_name", this.mExtensionElementName);
    localBundle.putString("ext_ns", this.mNamespace);
    localBundle.putString("ext_text", this.mText);
    Object localObject = new Bundle();
    String[] arrayOfString = this.mAttributeNames;
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      for (int i = 0;; i++)
      {
        arrayOfString = this.mAttributeNames;
        if (i >= arrayOfString.length) {
          break;
        }
        ((Bundle)localObject).putString(arrayOfString[i], this.mAttributeValues[i]);
      }
    }
    localBundle.putBundle("attributes", (Bundle)localObject);
    localObject = this.mChildrenEles;
    if ((localObject != null) && (((List)localObject).size() > 0)) {
      localBundle.putParcelableArray("children", toParcelableArray(this.mChildrenEles));
    }
    return localBundle;
  }
  
  public Parcelable toParcelable()
  {
    return toBundle();
  }
  
  public String toString()
  {
    return toXML();
  }
  
  public String toXML()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<");
    localStringBuilder.append(this.mExtensionElementName);
    if (!TextUtils.isEmpty(this.mNamespace))
    {
      localStringBuilder.append(" ");
      localStringBuilder.append("xmlns=");
      localStringBuilder.append("\"");
      localStringBuilder.append(this.mNamespace);
      localStringBuilder.append("\"");
    }
    Object localObject = this.mAttributeNames;
    if ((localObject != null) && (localObject.length > 0)) {
      for (int i = 0; i < this.mAttributeNames.length; i++) {
        if (!TextUtils.isEmpty(this.mAttributeValues[i]))
        {
          localStringBuilder.append(" ");
          localStringBuilder.append(this.mAttributeNames[i]);
          localStringBuilder.append("=\"");
          localStringBuilder.append(StringUtils.escapeForXML(this.mAttributeValues[i]));
          localStringBuilder.append("\"");
        }
      }
    }
    if (!TextUtils.isEmpty(this.mText))
    {
      localStringBuilder.append(">");
      localStringBuilder.append(this.mText);
      localStringBuilder.append("</");
      localStringBuilder.append(this.mExtensionElementName);
      localStringBuilder.append(">");
    }
    else
    {
      localObject = this.mChildrenEles;
      if ((localObject != null) && (((List)localObject).size() > 0))
      {
        localStringBuilder.append(">");
        localObject = this.mChildrenEles.iterator();
        while (((Iterator)localObject).hasNext()) {
          localStringBuilder.append(((CommonPacketExtension)((Iterator)localObject).next()).toXML());
        }
        localStringBuilder.append("</");
        localStringBuilder.append(this.mExtensionElementName);
        localStringBuilder.append(">");
      }
      else
      {
        localStringBuilder.append("/>");
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/CommonPacketExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */