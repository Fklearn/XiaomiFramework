package android.view.textclassifier;

import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TextLanguage
  implements Parcelable
{
  public static final Parcelable.Creator<TextLanguage> CREATOR = new Parcelable.Creator()
  {
    public TextLanguage createFromParcel(Parcel paramAnonymousParcel)
    {
      return TextLanguage.readFromParcel(paramAnonymousParcel);
    }
    
    public TextLanguage[] newArray(int paramAnonymousInt)
    {
      return new TextLanguage[paramAnonymousInt];
    }
  };
  static final TextLanguage EMPTY = new Builder().build();
  private final Bundle mBundle;
  private final EntityConfidence mEntityConfidence;
  private final String mId;
  
  private TextLanguage(String paramString, EntityConfidence paramEntityConfidence, Bundle paramBundle)
  {
    this.mId = paramString;
    this.mEntityConfidence = paramEntityConfidence;
    this.mBundle = paramBundle;
  }
  
  private static TextLanguage readFromParcel(Parcel paramParcel)
  {
    return new TextLanguage(paramParcel.readString(), (EntityConfidence)EntityConfidence.CREATOR.createFromParcel(paramParcel), paramParcel.readBundle());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public float getConfidenceScore(ULocale paramULocale)
  {
    return this.mEntityConfidence.getConfidenceScore(paramULocale.toLanguageTag());
  }
  
  public Bundle getExtras()
  {
    return this.mBundle;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public ULocale getLocale(int paramInt)
  {
    return ULocale.forLanguageTag((String)this.mEntityConfidence.getEntities().get(paramInt));
  }
  
  public int getLocaleHypothesisCount()
  {
    return this.mEntityConfidence.getEntities().size();
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextLanguage {id=%s, locales=%s, bundle=%s}", new Object[] { this.mId, this.mEntityConfidence, this.mBundle });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mId);
    this.mEntityConfidence.writeToParcel(paramParcel, paramInt);
    paramParcel.writeBundle(this.mBundle);
  }
  
  public static final class Builder
  {
    private Bundle mBundle;
    private final Map<String, Float> mEntityConfidenceMap = new ArrayMap();
    private String mId;
    
    public TextLanguage build()
    {
      Bundle localBundle1 = this.mBundle;
      Bundle localBundle2 = localBundle1;
      if (localBundle1 == null) {
        localBundle2 = Bundle.EMPTY;
      }
      this.mBundle = localBundle2;
      return new TextLanguage(this.mId, new EntityConfidence(this.mEntityConfidenceMap), this.mBundle, null);
    }
    
    public Builder putLocale(ULocale paramULocale, float paramFloat)
    {
      Preconditions.checkNotNull(paramULocale);
      this.mEntityConfidenceMap.put(paramULocale.toLanguageTag(), Float.valueOf(paramFloat));
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mBundle = ((Bundle)Preconditions.checkNotNull(paramBundle));
      return this;
    }
    
    public Builder setId(String paramString)
    {
      this.mId = paramString;
      return this;
    }
  }
  
  public static final class Request
    implements Parcelable
  {
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator()
    {
      public TextLanguage.Request createFromParcel(Parcel paramAnonymousParcel)
      {
        return TextLanguage.Request.readFromParcel(paramAnonymousParcel);
      }
      
      public TextLanguage.Request[] newArray(int paramAnonymousInt)
      {
        return new TextLanguage.Request[paramAnonymousInt];
      }
    };
    private String mCallingPackageName;
    private final Bundle mExtra;
    private final CharSequence mText;
    private int mUserId = 55536;
    
    private Request(CharSequence paramCharSequence, Bundle paramBundle)
    {
      this.mText = paramCharSequence;
      this.mExtra = paramBundle;
    }
    
    private static Request readFromParcel(Parcel paramParcel)
    {
      CharSequence localCharSequence = paramParcel.readCharSequence();
      String str = paramParcel.readString();
      int i = paramParcel.readInt();
      paramParcel = new Request(localCharSequence, paramParcel.readBundle());
      paramParcel.setCallingPackageName(str);
      paramParcel.setUserId(i);
      return paramParcel;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getCallingPackageName()
    {
      return this.mCallingPackageName;
    }
    
    public Bundle getExtras()
    {
      return this.mExtra;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public int getUserId()
    {
      return this.mUserId;
    }
    
    @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
    public void setCallingPackageName(String paramString)
    {
      this.mCallingPackageName = paramString;
    }
    
    void setUserId(int paramInt)
    {
      this.mUserId = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeCharSequence(this.mText);
      paramParcel.writeString(this.mCallingPackageName);
      paramParcel.writeInt(this.mUserId);
      paramParcel.writeBundle(this.mExtra);
    }
    
    public static final class Builder
    {
      private Bundle mBundle;
      private final CharSequence mText;
      
      public Builder(CharSequence paramCharSequence)
      {
        this.mText = ((CharSequence)Preconditions.checkNotNull(paramCharSequence));
      }
      
      public TextLanguage.Request build()
      {
        String str = this.mText.toString();
        Bundle localBundle1 = this.mBundle;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new TextLanguage.Request(str, localBundle2, null);
      }
      
      public Builder setExtras(Bundle paramBundle)
      {
        this.mBundle = ((Bundle)Preconditions.checkNotNull(paramBundle));
        return this;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextLanguage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */