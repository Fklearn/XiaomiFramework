package android.view.textclassifier;

import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannedString;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TextSelection
  implements Parcelable
{
  public static final Parcelable.Creator<TextSelection> CREATOR = new Parcelable.Creator()
  {
    public TextSelection createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextSelection(paramAnonymousParcel, null);
    }
    
    public TextSelection[] newArray(int paramAnonymousInt)
    {
      return new TextSelection[paramAnonymousInt];
    }
  };
  private final int mEndIndex;
  private final EntityConfidence mEntityConfidence;
  private final Bundle mExtras;
  private final String mId;
  private final int mStartIndex;
  
  private TextSelection(int paramInt1, int paramInt2, Map<String, Float> paramMap, String paramString, Bundle paramBundle)
  {
    this.mStartIndex = paramInt1;
    this.mEndIndex = paramInt2;
    this.mEntityConfidence = new EntityConfidence(paramMap);
    this.mId = paramString;
    this.mExtras = paramBundle;
  }
  
  private TextSelection(Parcel paramParcel)
  {
    this.mStartIndex = paramParcel.readInt();
    this.mEndIndex = paramParcel.readInt();
    this.mEntityConfidence = ((EntityConfidence)EntityConfidence.CREATOR.createFromParcel(paramParcel));
    this.mId = paramParcel.readString();
    this.mExtras = paramParcel.readBundle();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public float getConfidenceScore(String paramString)
  {
    return this.mEntityConfidence.getConfidenceScore(paramString);
  }
  
  public String getEntity(int paramInt)
  {
    return (String)this.mEntityConfidence.getEntities().get(paramInt);
  }
  
  public int getEntityCount()
  {
    return this.mEntityConfidence.getEntities().size();
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public int getSelectionEndIndex()
  {
    return this.mEndIndex;
  }
  
  public int getSelectionStartIndex()
  {
    return this.mStartIndex;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextSelection {id=%s, startIndex=%d, endIndex=%d, entities=%s}", new Object[] { this.mId, Integer.valueOf(this.mStartIndex), Integer.valueOf(this.mEndIndex), this.mEntityConfidence });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mStartIndex);
    paramParcel.writeInt(this.mEndIndex);
    this.mEntityConfidence.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mId);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static final class Builder
  {
    private final int mEndIndex;
    private final Map<String, Float> mEntityConfidence = new ArrayMap();
    private Bundle mExtras;
    private String mId;
    private final int mStartIndex;
    
    public Builder(int paramInt1, int paramInt2)
    {
      boolean bool1 = true;
      boolean bool2;
      if (paramInt1 >= 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt2 > paramInt1) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      this.mStartIndex = paramInt1;
      this.mEndIndex = paramInt2;
    }
    
    public TextSelection build()
    {
      int i = this.mStartIndex;
      int j = this.mEndIndex;
      Map localMap = this.mEntityConfidence;
      String str = this.mId;
      Bundle localBundle1 = this.mExtras;
      Bundle localBundle2 = localBundle1;
      if (localBundle1 == null) {
        localBundle2 = Bundle.EMPTY;
      }
      return new TextSelection(i, j, localMap, str, localBundle2, null);
    }
    
    public Builder setEntityType(String paramString, float paramFloat)
    {
      Preconditions.checkNotNull(paramString);
      this.mEntityConfidence.put(paramString, Float.valueOf(paramFloat));
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
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
      public TextSelection.Request createFromParcel(Parcel paramAnonymousParcel)
      {
        return TextSelection.Request.readFromParcel(paramAnonymousParcel);
      }
      
      public TextSelection.Request[] newArray(int paramAnonymousInt)
      {
        return new TextSelection.Request[paramAnonymousInt];
      }
    };
    private String mCallingPackageName;
    private final boolean mDarkLaunchAllowed;
    private final LocaleList mDefaultLocales;
    private final int mEndIndex;
    private final Bundle mExtras;
    private final int mStartIndex;
    private final CharSequence mText;
    private int mUserId = 55536;
    
    private Request(CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList, boolean paramBoolean, Bundle paramBundle)
    {
      this.mText = paramCharSequence;
      this.mStartIndex = paramInt1;
      this.mEndIndex = paramInt2;
      this.mDefaultLocales = paramLocaleList;
      this.mDarkLaunchAllowed = paramBoolean;
      this.mExtras = paramBundle;
    }
    
    private static Request readFromParcel(Parcel paramParcel)
    {
      CharSequence localCharSequence = paramParcel.readCharSequence();
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      LocaleList localLocaleList = (LocaleList)paramParcel.readParcelable(null);
      String str = paramParcel.readString();
      int k = paramParcel.readInt();
      paramParcel = new Request(localCharSequence, i, j, localLocaleList, false, paramParcel.readBundle());
      paramParcel.setCallingPackageName(str);
      paramParcel.setUserId(k);
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
    
    public LocaleList getDefaultLocales()
    {
      return this.mDefaultLocales;
    }
    
    public int getEndIndex()
    {
      return this.mEndIndex;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public int getStartIndex()
    {
      return this.mStartIndex;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public int getUserId()
    {
      return this.mUserId;
    }
    
    public boolean isDarkLaunchAllowed()
    {
      return this.mDarkLaunchAllowed;
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
      paramParcel.writeInt(this.mStartIndex);
      paramParcel.writeInt(this.mEndIndex);
      paramParcel.writeParcelable(this.mDefaultLocales, paramInt);
      paramParcel.writeString(this.mCallingPackageName);
      paramParcel.writeInt(this.mUserId);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private boolean mDarkLaunchAllowed;
      private LocaleList mDefaultLocales;
      private final int mEndIndex;
      private Bundle mExtras;
      private final int mStartIndex;
      private final CharSequence mText;
      
      public Builder(CharSequence paramCharSequence, int paramInt1, int paramInt2)
      {
        TextClassifier.Utils.checkArgument(paramCharSequence, paramInt1, paramInt2);
        this.mText = paramCharSequence;
        this.mStartIndex = paramInt1;
        this.mEndIndex = paramInt2;
      }
      
      public TextSelection.Request build()
      {
        SpannedString localSpannedString = new SpannedString(this.mText);
        int i = this.mStartIndex;
        int j = this.mEndIndex;
        LocaleList localLocaleList = this.mDefaultLocales;
        boolean bool = this.mDarkLaunchAllowed;
        Bundle localBundle1 = this.mExtras;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new TextSelection.Request(localSpannedString, i, j, localLocaleList, bool, localBundle2, null);
      }
      
      public Builder setDarkLaunchAllowed(boolean paramBoolean)
      {
        this.mDarkLaunchAllowed = paramBoolean;
        return this;
      }
      
      public Builder setDefaultLocales(LocaleList paramLocaleList)
      {
        this.mDefaultLocales = paramLocaleList;
        return this;
      }
      
      public Builder setExtras(Bundle paramBundle)
      {
        this.mExtras = paramBundle;
        return this;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */