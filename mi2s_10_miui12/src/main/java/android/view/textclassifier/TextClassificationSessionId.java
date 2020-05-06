package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.Locale;
import java.util.UUID;

public final class TextClassificationSessionId
  implements Parcelable
{
  public static final Parcelable.Creator<TextClassificationSessionId> CREATOR = new Parcelable.Creator()
  {
    public TextClassificationSessionId createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextClassificationSessionId((String)Preconditions.checkNotNull(paramAnonymousParcel.readString()));
    }
    
    public TextClassificationSessionId[] newArray(int paramAnonymousInt)
    {
      return new TextClassificationSessionId[paramAnonymousInt];
    }
  };
  private final String mValue;
  
  public TextClassificationSessionId()
  {
    this(UUID.randomUUID().toString());
  }
  
  public TextClassificationSessionId(String paramString)
  {
    this.mValue = paramString;
  }
  
  public static TextClassificationSessionId unflattenFromString(String paramString)
  {
    return new TextClassificationSessionId(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (TextClassificationSessionId)paramObject;
    return this.mValue.equals(((TextClassificationSessionId)paramObject).mValue);
  }
  
  public String flattenToString()
  {
    return this.mValue;
  }
  
  public int hashCode()
  {
    return 1 * 31 + this.mValue.hashCode();
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextClassificationSessionId {%s}", new Object[] { this.mValue });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mValue);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassificationSessionId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */