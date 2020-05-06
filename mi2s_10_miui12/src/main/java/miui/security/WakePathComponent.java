package miui.security;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class WakePathComponent
  implements Parcelable
{
  public static final Parcelable.Creator<WakePathComponent> CREATOR = new Parcelable.Creator()
  {
    public WakePathComponent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WakePathComponent(paramAnonymousParcel, null);
    }
    
    public WakePathComponent[] newArray(int paramAnonymousInt)
    {
      return new WakePathComponent[paramAnonymousInt];
    }
  };
  public static final int WAKE_PATH_COMPONENT_ACTIVITY = 3;
  public static final int WAKE_PATH_COMPONENT_PROVIDER = 4;
  public static final int WAKE_PATH_COMPONENT_RECEIVER = 1;
  public static final int WAKE_PATH_COMPONENT_SERVICE = 2;
  private String mClassname;
  private List<String> mIntentActions = new ArrayList();
  private int mType;
  
  public WakePathComponent() {}
  
  public WakePathComponent(int paramInt, String paramString, List<String> paramList)
    throws Exception
  {
    this.mType = paramInt;
    this.mClassname = paramString;
    this.mIntentActions.addAll(paramList);
  }
  
  private WakePathComponent(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mClassname = paramParcel.readString();
    paramParcel.readStringList(this.mIntentActions);
  }
  
  public void addIntentAction(String paramString)
  {
    this.mIntentActions.add(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getClassname()
  {
    return this.mClassname;
  }
  
  public List<String> getIntentActions()
  {
    return this.mIntentActions;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void setClassname(String paramString)
  {
    this.mClassname = paramString;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("WakePathComponent: mType=");
    localStringBuilder.append(this.mType);
    localStringBuilder.append(" mClassname=");
    localStringBuilder.append(this.mClassname);
    localStringBuilder.append(" mIntentActions=");
    localStringBuilder.append(this.mIntentActions.toString());
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeString(this.mClassname);
    paramParcel.writeStringList(this.mIntentActions);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/WakePathComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */