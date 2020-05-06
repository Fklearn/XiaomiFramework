package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.Locale;

public final class TextClassificationContext
  implements Parcelable
{
  public static final Parcelable.Creator<TextClassificationContext> CREATOR = new Parcelable.Creator()
  {
    public TextClassificationContext createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextClassificationContext(paramAnonymousParcel, null);
    }
    
    public TextClassificationContext[] newArray(int paramAnonymousInt)
    {
      return new TextClassificationContext[paramAnonymousInt];
    }
  };
  private final String mPackageName;
  private int mUserId = 55536;
  private final String mWidgetType;
  private final String mWidgetVersion;
  
  private TextClassificationContext(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mWidgetType = paramParcel.readString();
    this.mWidgetVersion = paramParcel.readString();
    this.mUserId = paramParcel.readInt();
  }
  
  private TextClassificationContext(String paramString1, String paramString2, String paramString3)
  {
    this.mPackageName = ((String)Preconditions.checkNotNull(paramString1));
    this.mWidgetType = ((String)Preconditions.checkNotNull(paramString2));
    this.mWidgetVersion = paramString3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public String getWidgetType()
  {
    return this.mWidgetType;
  }
  
  public String getWidgetVersion()
  {
    return this.mWidgetVersion;
  }
  
  void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextClassificationContext{packageName=%s, widgetType=%s, widgetVersion=%s, userId=%d}", new Object[] { this.mPackageName, this.mWidgetType, this.mWidgetVersion, Integer.valueOf(this.mUserId) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeString(this.mWidgetType);
    paramParcel.writeString(this.mWidgetVersion);
    paramParcel.writeInt(this.mUserId);
  }
  
  public static final class Builder
  {
    private final String mPackageName;
    private final String mWidgetType;
    private String mWidgetVersion;
    
    public Builder(String paramString1, String paramString2)
    {
      this.mPackageName = ((String)Preconditions.checkNotNull(paramString1));
      this.mWidgetType = ((String)Preconditions.checkNotNull(paramString2));
    }
    
    public TextClassificationContext build()
    {
      return new TextClassificationContext(this.mPackageName, this.mWidgetType, this.mWidgetVersion, null);
    }
    
    public Builder setWidgetVersion(String paramString)
    {
      this.mWidgetVersion = paramString;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassificationContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */