package miui.security;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Slog;

public class WakePathRuleInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WakePathRuleInfo> CREATOR = new Parcelable.Creator()
  {
    public WakePathRuleInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WakePathRuleInfo(paramAnonymousParcel, null);
    }
    
    public WakePathRuleInfo[] newArray(int paramAnonymousInt)
    {
      return new WakePathRuleInfo[paramAnonymousInt];
    }
  };
  private static final int EXPRESS_TYPE_WILDCARD_ALL = 2;
  private static final int EXPRESS_TYPE_WILDCARD_END = 1;
  private static final int EXPRESS_TYPE_WILDCARD_NONE = 0;
  private static final int EXPRESS_TYPE_WILDCARD_OTHER = 3;
  private static final String EXPRESS_WILDCARD = "*";
  private static final String TAG = WakePathRuleInfo.class.getName();
  public static final int WAKE_TYPE_ALLOW_START_ACTIVITY = 17;
  public static final int WAKE_TYPE_CALL_LIST = 16;
  public static final int WAKE_TYPE_GET_CONTENT_PROVIDER = 4;
  public static final int WAKE_TYPE_SEND_BROADCAST = 2;
  public static final int WAKE_TYPE_START_ACTIVITY = 1;
  public static final int WAKE_TYPE_START_SERVICE = 8;
  public static final int WAKE_TYPE_WHITE_BLACK_OFFSET = 5;
  public static final int WAKE_TYPE_WHITE_GET_CONTENT_PROVIDER = 128;
  public static final int WAKE_TYPE_WHITE_SEND_BROADCAST = 64;
  public static final int WAKE_TYPE_WHITE_START_ACTIVITY = 32;
  public static final int WAKE_TYPE_WHITE_START_SERVICE = 256;
  public String mActionExpress;
  private int mActionExpressType;
  public String mCalleeExpress;
  private int mCalleeExpressType;
  public String mCallerExpress;
  private int mCallerExpressType;
  public String mClassNameExpress;
  private int mClassNameExpressType;
  public int mHashCode;
  public int mUserSettings;
  public int mWakeType;
  
  private WakePathRuleInfo(Parcel paramParcel)
  {
    this.mActionExpress = paramParcel.readString();
    this.mActionExpressType = paramParcel.readInt();
    this.mClassNameExpress = paramParcel.readString();
    this.mClassNameExpressType = paramParcel.readInt();
    this.mCallerExpress = paramParcel.readString();
    this.mCallerExpressType = paramParcel.readInt();
    this.mCalleeExpress = paramParcel.readString();
    this.mCalleeExpressType = paramParcel.readInt();
    this.mWakeType = paramParcel.readInt();
    this.mUserSettings = paramParcel.readInt();
    this.mHashCode = paramParcel.readInt();
  }
  
  public WakePathRuleInfo(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
    throws Exception
  {
    this.mActionExpress = paramString1;
    this.mActionExpressType = getExpressType(this.mActionExpress);
    this.mClassNameExpress = paramString2;
    this.mClassNameExpressType = getExpressType(this.mClassNameExpress);
    this.mCallerExpress = paramString3;
    this.mCallerExpressType = getExpressType(this.mCallerExpress);
    this.mCalleeExpress = paramString4;
    this.mCalleeExpressType = getExpressType(this.mCalleeExpress);
    this.mWakeType = paramInt1;
    this.mUserSettings = paramInt2;
    if (this.mWakeType == 16) {
      this.mHashCode = getHashCode(paramString1, paramString2, paramString3, paramString4);
    } else {
      this.mHashCode = 0;
    }
  }
  
  public static boolean checkCompatibility(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    if (paramInt == 17) {
      return true;
    }
    if ((TextUtils.equals(paramString1, "*")) && (TextUtils.equals(paramString2, "*")) && (TextUtils.equals(paramString3, "*")) && (TextUtils.equals(paramString4, "*"))) {
      return false;
    }
    if (paramString1.length() + paramString2.length() + paramString3.length() + paramString4.length() < 10) {
      return false;
    }
    if ((!TextUtils.equals(paramString3, "com.miui.home")) && (!TextUtils.equals(paramString4, "com.miui.home")))
    {
      if ((TextUtils.equals(paramString3, "android")) || (TextUtils.equals(paramString4, "android")))
      {
        if (TextUtils.equals(paramString4, "android")) {
          return false;
        }
        if ((TextUtils.equals(paramString1, "*")) && (TextUtils.equals(paramString2, "*"))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private static boolean expressCompare(String paramString1, int paramInt, String paramString2)
  {
    if (paramInt != 0)
    {
      String str;
      if (paramInt != 1)
      {
        if (paramInt == 3) {
          if ((!TextUtils.isEmpty(paramString2)) && (!TextUtils.isEmpty(paramString1)))
          {
            paramInt = paramString1.indexOf("*");
            if (paramInt == -1) {
              return false;
            }
            str = paramString1.substring(0, paramInt);
            paramString1 = paramString1.substring(paramInt + 1);
            if ((!TextUtils.isEmpty(str)) && (!TextUtils.isEmpty(paramString1)))
            {
              if (!paramString2.startsWith(str)) {
                return false;
              }
              if (!paramString2.endsWith(paramString1)) {
                return false;
              }
            }
            else
            {
              return false;
            }
          }
          else
          {
            return false;
          }
        }
      }
      else
      {
        str = paramString1;
        if (paramString1.length() >= 2) {
          str = paramString1.substring(0, paramString1.length() - 2);
        }
        if ((TextUtils.isEmpty(paramString2)) || (!paramString2.startsWith(str))) {
          return false;
        }
      }
    }
    else if (!TextUtils.equals(paramString1, paramString2))
    {
      return false;
    }
    return true;
  }
  
  private static int getExpressType(String paramString)
  {
    int i = 0;
    if (TextUtils.isEmpty(paramString)) {
      i = 0;
    } else if (paramString.equals("*")) {
      i = 2;
    } else if (paramString.endsWith("*")) {
      i = 1;
    } else if (paramString.contains("*")) {
      i = 3;
    }
    return i;
  }
  
  public static int getHashCode(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramString1);
    localStringBuffer.append(paramString2);
    localStringBuffer.append(paramString3);
    localStringBuffer.append(paramString4);
    return localStringBuffer.toString().hashCode();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(int paramInt)
  {
    int i = this.mWakeType;
    boolean bool = false;
    if ((i == 16) && (paramInt != 0))
    {
      if (this.mHashCode == paramInt) {
        bool = true;
      }
      return bool;
    }
    String str = TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MIUILOG-WAKEPATH equals: Invalid parameter!! mWakeType=");
    localStringBuilder.append(this.mWakeType);
    localStringBuilder.append(" hashCode=");
    localStringBuilder.append(paramInt);
    Slog.w(str, localStringBuilder.toString());
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == null) {
      return false;
    }
    try
    {
      paramObject = (WakePathRuleInfo)paramObject;
      if ((TextUtils.equals(this.mActionExpress, ((WakePathRuleInfo)paramObject).mActionExpress)) && (TextUtils.equals(this.mClassNameExpress, ((WakePathRuleInfo)paramObject).mClassNameExpress)) && (TextUtils.equals(this.mCallerExpress, ((WakePathRuleInfo)paramObject).mCallerExpress)) && (TextUtils.equals(this.mCalleeExpress, ((WakePathRuleInfo)paramObject).mCalleeExpress)) && (this.mWakeType == ((WakePathRuleInfo)paramObject).mWakeType)) {
        bool = true;
      }
      return bool;
    }
    catch (ClassCastException paramObject)
    {
      ((ClassCastException)paramObject).printStackTrace();
    }
    return false;
  }
  
  public boolean equals(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    if ((this.mWakeType & paramInt) == 0) {
      return false;
    }
    if (!expressCompare(this.mActionExpress, this.mActionExpressType, paramString1)) {
      return false;
    }
    if (!expressCompare(this.mClassNameExpress, this.mClassNameExpressType, paramString2)) {
      return false;
    }
    if (!expressCompare(this.mCallerExpress, this.mCallerExpressType, paramString3)) {
      return false;
    }
    return expressCompare(this.mCalleeExpress, this.mCalleeExpressType, paramString4);
  }
  
  public String getCalleeExpress()
  {
    return this.mCalleeExpress;
  }
  
  public String getCallerExpress()
  {
    return this.mCallerExpress;
  }
  
  public int getUserSettings()
  {
    return this.mUserSettings;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("WakePathRuleInfo: mActionExpress=");
    localStringBuilder.append(this.mActionExpress);
    localStringBuilder.append(" mClassNameExpress=");
    localStringBuilder.append(this.mClassNameExpress);
    localStringBuilder.append(" mCallerExpress=");
    localStringBuilder.append(this.mCallerExpress);
    localStringBuilder.append(" mCalleeExpress= ");
    localStringBuilder.append(this.mCalleeExpress);
    localStringBuilder.append(" mWakeType=");
    localStringBuilder.append(this.mWakeType);
    localStringBuilder.append(" userSettings=");
    localStringBuilder.append(this.mUserSettings);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mActionExpress);
    paramParcel.writeInt(this.mActionExpressType);
    paramParcel.writeString(this.mClassNameExpress);
    paramParcel.writeInt(this.mClassNameExpressType);
    paramParcel.writeString(this.mCallerExpress);
    paramParcel.writeInt(this.mCallerExpressType);
    paramParcel.writeString(this.mCalleeExpress);
    paramParcel.writeInt(this.mCalleeExpressType);
    paramParcel.writeInt(this.mWakeType);
    paramParcel.writeInt(this.mUserSettings);
    paramParcel.writeInt(this.mHashCode);
  }
  
  public static class UserSettings
  {
    public static final int ACCEPT = 1;
    public static final int REJECT = 2;
    public static final int UNDEF = 0;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/WakePathRuleInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */