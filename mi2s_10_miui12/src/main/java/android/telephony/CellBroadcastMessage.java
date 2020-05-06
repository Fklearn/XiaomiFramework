package android.telephony;

import android.annotation.UnsupportedAppUsage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateUtils;

public class CellBroadcastMessage
  implements Parcelable
{
  public static final Parcelable.Creator<CellBroadcastMessage> CREATOR = new Parcelable.Creator()
  {
    public CellBroadcastMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CellBroadcastMessage(paramAnonymousParcel, null);
    }
    
    public CellBroadcastMessage[] newArray(int paramAnonymousInt)
    {
      return new CellBroadcastMessage[paramAnonymousInt];
    }
  };
  public static final String SMS_CB_MESSAGE_EXTRA = "com.android.cellbroadcastreceiver.SMS_CB_MESSAGE";
  private final long mDeliveryTime;
  private boolean mIsRead;
  private final SmsCbMessage mSmsCbMessage;
  private int mSubId;
  
  private CellBroadcastMessage(Parcel paramParcel)
  {
    boolean bool = false;
    this.mSubId = 0;
    this.mSmsCbMessage = new SmsCbMessage(paramParcel);
    this.mDeliveryTime = paramParcel.readLong();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.mIsRead = bool;
    this.mSubId = paramParcel.readInt();
  }
  
  @UnsupportedAppUsage
  public CellBroadcastMessage(SmsCbMessage paramSmsCbMessage)
  {
    this.mSubId = 0;
    this.mSmsCbMessage = paramSmsCbMessage;
    this.mDeliveryTime = System.currentTimeMillis();
    this.mIsRead = false;
  }
  
  private CellBroadcastMessage(SmsCbMessage paramSmsCbMessage, long paramLong, boolean paramBoolean)
  {
    this.mSubId = 0;
    this.mSmsCbMessage = paramSmsCbMessage;
    this.mDeliveryTime = paramLong;
    this.mIsRead = paramBoolean;
  }
  
  @UnsupportedAppUsage
  public static CellBroadcastMessage createFromCursor(Cursor paramCursor)
  {
    int i = paramCursor.getInt(paramCursor.getColumnIndexOrThrow("geo_scope"));
    int j = paramCursor.getInt(paramCursor.getColumnIndexOrThrow("serial_number"));
    int k = paramCursor.getInt(paramCursor.getColumnIndexOrThrow("service_category"));
    String str1 = paramCursor.getString(paramCursor.getColumnIndexOrThrow("language"));
    String str2 = paramCursor.getString(paramCursor.getColumnIndexOrThrow("body"));
    int m = paramCursor.getInt(paramCursor.getColumnIndexOrThrow("format"));
    int n = paramCursor.getInt(paramCursor.getColumnIndexOrThrow("priority"));
    int i1 = paramCursor.getColumnIndex("plmn");
    if ((i1 != -1) && (!paramCursor.isNull(i1))) {
      localObject = paramCursor.getString(i1);
    } else {
      localObject = null;
    }
    i1 = paramCursor.getColumnIndex("lac");
    if ((i1 != -1) && (!paramCursor.isNull(i1))) {
      i1 = paramCursor.getInt(i1);
    } else {
      i1 = -1;
    }
    int i2 = paramCursor.getColumnIndex("cid");
    if ((i2 != -1) && (!paramCursor.isNull(i2))) {
      i2 = paramCursor.getInt(i2);
    } else {
      i2 = -1;
    }
    SmsCbLocation localSmsCbLocation = new SmsCbLocation((String)localObject, i1, i2);
    i1 = paramCursor.getColumnIndex("etws_warning_type");
    if ((i1 != -1) && (!paramCursor.isNull(i1))) {
      localObject = new SmsCbEtwsInfo(paramCursor.getInt(i1), false, false, false, null);
    } else {
      localObject = null;
    }
    i1 = paramCursor.getColumnIndex("cmas_message_class");
    SmsCbCmasInfo localSmsCbCmasInfo;
    if ((i1 != -1) && (!paramCursor.isNull(i1)))
    {
      int i3 = paramCursor.getInt(i1);
      i1 = paramCursor.getColumnIndex("cmas_category");
      if ((i1 != -1) && (!paramCursor.isNull(i1))) {
        i1 = paramCursor.getInt(i1);
      } else {
        i1 = -1;
      }
      i2 = paramCursor.getColumnIndex("cmas_response_type");
      if ((i2 != -1) && (!paramCursor.isNull(i2))) {
        i2 = paramCursor.getInt(i2);
      } else {
        i2 = -1;
      }
      int i4 = paramCursor.getColumnIndex("cmas_severity");
      if ((i4 != -1) && (!paramCursor.isNull(i4))) {
        i4 = paramCursor.getInt(i4);
      } else {
        i4 = -1;
      }
      int i5 = paramCursor.getColumnIndex("cmas_urgency");
      if ((i5 != -1) && (!paramCursor.isNull(i5))) {
        i5 = paramCursor.getInt(i5);
      } else {
        i5 = -1;
      }
      int i6 = paramCursor.getColumnIndex("cmas_certainty");
      if ((i6 != -1) && (!paramCursor.isNull(i6))) {
        i6 = paramCursor.getInt(i6);
      } else {
        i6 = -1;
      }
      localSmsCbCmasInfo = new SmsCbCmasInfo(i3, i1, i2, i4, i5, i6);
    }
    else
    {
      localSmsCbCmasInfo = null;
    }
    Object localObject = new SmsCbMessage(m, i, j, localSmsCbLocation, k, str1, str2, n, (SmsCbEtwsInfo)localObject, localSmsCbCmasInfo);
    long l = paramCursor.getLong(paramCursor.getColumnIndexOrThrow("date"));
    boolean bool;
    if (paramCursor.getInt(paramCursor.getColumnIndexOrThrow("read")) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return new CellBroadcastMessage((SmsCbMessage)localObject, l, bool);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCmasMessageClass()
  {
    if (this.mSmsCbMessage.isCmasMessage()) {
      return this.mSmsCbMessage.getCmasWarningInfo().getMessageClass();
    }
    return -1;
  }
  
  public SmsCbCmasInfo getCmasWarningInfo()
  {
    return this.mSmsCbMessage.getCmasWarningInfo();
  }
  
  @UnsupportedAppUsage
  public ContentValues getContentValues()
  {
    ContentValues localContentValues = new ContentValues(16);
    Object localObject = this.mSmsCbMessage;
    localContentValues.put("geo_scope", Integer.valueOf(((SmsCbMessage)localObject).getGeographicalScope()));
    SmsCbLocation localSmsCbLocation = ((SmsCbMessage)localObject).getLocation();
    if (localSmsCbLocation.getPlmn() != null) {
      localContentValues.put("plmn", localSmsCbLocation.getPlmn());
    }
    if (localSmsCbLocation.getLac() != -1) {
      localContentValues.put("lac", Integer.valueOf(localSmsCbLocation.getLac()));
    }
    if (localSmsCbLocation.getCid() != -1) {
      localContentValues.put("cid", Integer.valueOf(localSmsCbLocation.getCid()));
    }
    localContentValues.put("serial_number", Integer.valueOf(((SmsCbMessage)localObject).getSerialNumber()));
    localContentValues.put("service_category", Integer.valueOf(((SmsCbMessage)localObject).getServiceCategory()));
    localContentValues.put("language", ((SmsCbMessage)localObject).getLanguageCode());
    localContentValues.put("body", ((SmsCbMessage)localObject).getMessageBody());
    localContentValues.put("date", Long.valueOf(this.mDeliveryTime));
    localContentValues.put("read", Boolean.valueOf(this.mIsRead));
    localContentValues.put("format", Integer.valueOf(((SmsCbMessage)localObject).getMessageFormat()));
    localContentValues.put("priority", Integer.valueOf(((SmsCbMessage)localObject).getMessagePriority()));
    localObject = this.mSmsCbMessage.getEtwsWarningInfo();
    if (localObject != null) {
      localContentValues.put("etws_warning_type", Integer.valueOf(((SmsCbEtwsInfo)localObject).getWarningType()));
    }
    localObject = this.mSmsCbMessage.getCmasWarningInfo();
    if (localObject != null)
    {
      localContentValues.put("cmas_message_class", Integer.valueOf(((SmsCbCmasInfo)localObject).getMessageClass()));
      localContentValues.put("cmas_category", Integer.valueOf(((SmsCbCmasInfo)localObject).getCategory()));
      localContentValues.put("cmas_response_type", Integer.valueOf(((SmsCbCmasInfo)localObject).getResponseType()));
      localContentValues.put("cmas_severity", Integer.valueOf(((SmsCbCmasInfo)localObject).getSeverity()));
      localContentValues.put("cmas_urgency", Integer.valueOf(((SmsCbCmasInfo)localObject).getUrgency()));
      localContentValues.put("cmas_certainty", Integer.valueOf(((SmsCbCmasInfo)localObject).getCertainty()));
    }
    return localContentValues;
  }
  
  public String getDateString(Context paramContext)
  {
    return DateUtils.formatDateTime(paramContext, this.mDeliveryTime, 527121);
  }
  
  @UnsupportedAppUsage
  public long getDeliveryTime()
  {
    return this.mDeliveryTime;
  }
  
  @UnsupportedAppUsage
  public SmsCbEtwsInfo getEtwsWarningInfo()
  {
    return this.mSmsCbMessage.getEtwsWarningInfo();
  }
  
  @UnsupportedAppUsage
  public String getLanguageCode()
  {
    return this.mSmsCbMessage.getLanguageCode();
  }
  
  @UnsupportedAppUsage
  public String getMessageBody()
  {
    return this.mSmsCbMessage.getMessageBody();
  }
  
  @UnsupportedAppUsage
  public int getSerialNumber()
  {
    return this.mSmsCbMessage.getSerialNumber();
  }
  
  @UnsupportedAppUsage
  public int getServiceCategory()
  {
    return this.mSmsCbMessage.getServiceCategory();
  }
  
  @UnsupportedAppUsage
  public String getSpokenDateString(Context paramContext)
  {
    return DateUtils.formatDateTime(paramContext, this.mDeliveryTime, 17);
  }
  
  public int getSubId()
  {
    return this.mSubId;
  }
  
  @UnsupportedAppUsage
  public boolean isCmasMessage()
  {
    return this.mSmsCbMessage.isCmasMessage();
  }
  
  @UnsupportedAppUsage
  public boolean isEmergencyAlertMessage()
  {
    return this.mSmsCbMessage.isEmergencyMessage();
  }
  
  public boolean isEtwsEmergencyUserAlert()
  {
    SmsCbEtwsInfo localSmsCbEtwsInfo = this.mSmsCbMessage.getEtwsWarningInfo();
    boolean bool;
    if ((localSmsCbEtwsInfo != null) && (localSmsCbEtwsInfo.isEmergencyUserAlert())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isEtwsMessage()
  {
    return this.mSmsCbMessage.isEtwsMessage();
  }
  
  public boolean isEtwsPopupAlert()
  {
    SmsCbEtwsInfo localSmsCbEtwsInfo = this.mSmsCbMessage.getEtwsWarningInfo();
    boolean bool;
    if ((localSmsCbEtwsInfo != null) && (localSmsCbEtwsInfo.isPopupAlert())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEtwsTestMessage()
  {
    SmsCbEtwsInfo localSmsCbEtwsInfo = this.mSmsCbMessage.getEtwsWarningInfo();
    boolean bool;
    if ((localSmsCbEtwsInfo != null) && (localSmsCbEtwsInfo.getWarningType() == 3)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isPublicAlertMessage()
  {
    return this.mSmsCbMessage.isEmergencyMessage();
  }
  
  @UnsupportedAppUsage
  public boolean isRead()
  {
    return this.mIsRead;
  }
  
  public void setIsRead(boolean paramBoolean)
  {
    this.mIsRead = paramBoolean;
  }
  
  public void setSubId(int paramInt)
  {
    this.mSubId = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mSmsCbMessage.writeToParcel(paramParcel, paramInt);
    paramParcel.writeLong(this.mDeliveryTime);
    paramParcel.writeInt(this.mIsRead);
    paramParcel.writeInt(this.mSubId);
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/telephony/CellBroadcastMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */