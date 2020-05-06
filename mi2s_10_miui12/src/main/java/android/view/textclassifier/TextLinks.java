package android.view.textclassifier;

import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class TextLinks
  implements Parcelable
{
  public static final int APPLY_STRATEGY_IGNORE = 0;
  public static final int APPLY_STRATEGY_REPLACE = 1;
  public static final Parcelable.Creator<TextLinks> CREATOR = new Parcelable.Creator()
  {
    public TextLinks createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextLinks(paramAnonymousParcel, null);
    }
    
    public TextLinks[] newArray(int paramAnonymousInt)
    {
      return new TextLinks[paramAnonymousInt];
    }
  };
  public static final int STATUS_DIFFERENT_TEXT = 3;
  public static final int STATUS_LINKS_APPLIED = 0;
  public static final int STATUS_NO_LINKS_APPLIED = 2;
  public static final int STATUS_NO_LINKS_FOUND = 1;
  public static final int STATUS_UNSUPPORTED_CHARACTER = 4;
  private final Bundle mExtras;
  private final String mFullText;
  private final List<TextLink> mLinks;
  
  private TextLinks(Parcel paramParcel)
  {
    this.mFullText = paramParcel.readString();
    this.mLinks = paramParcel.createTypedArrayList(TextLink.CREATOR);
    this.mExtras = paramParcel.readBundle();
  }
  
  private TextLinks(String paramString, ArrayList<TextLink> paramArrayList, Bundle paramBundle)
  {
    this.mFullText = paramString;
    this.mLinks = Collections.unmodifiableList(paramArrayList);
    this.mExtras = paramBundle;
  }
  
  public int apply(Spannable paramSpannable, int paramInt, Function<TextLink, TextLinkSpan> paramFunction)
  {
    Preconditions.checkNotNull(paramSpannable);
    return new TextLinksParams.Builder().setApplyStrategy(paramInt).setSpanFactory(paramFunction).build().apply(paramSpannable, this);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public Collection<TextLink> getLinks()
  {
    return this.mLinks;
  }
  
  public String getText()
  {
    return this.mFullText;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextLinks{fullText=%s, links=%s}", new Object[] { this.mFullText, this.mLinks });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mFullText);
    paramParcel.writeTypedList(this.mLinks);
    paramParcel.writeBundle(this.mExtras);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ApplyStrategy {}
  
  public static final class Builder
  {
    private Bundle mExtras;
    private final String mFullText;
    private final ArrayList<TextLinks.TextLink> mLinks;
    
    public Builder(String paramString)
    {
      this.mFullText = ((String)Preconditions.checkNotNull(paramString));
      this.mLinks = new ArrayList();
    }
    
    private Builder addLink(int paramInt1, int paramInt2, Map<String, Float> paramMap, Bundle paramBundle, URLSpan paramURLSpan)
    {
      this.mLinks.add(new TextLinks.TextLink(paramInt1, paramInt2, new EntityConfidence(paramMap), paramBundle, paramURLSpan, null));
      return this;
    }
    
    public Builder addLink(int paramInt1, int paramInt2, Map<String, Float> paramMap)
    {
      return addLink(paramInt1, paramInt2, paramMap, Bundle.EMPTY, null);
    }
    
    public Builder addLink(int paramInt1, int paramInt2, Map<String, Float> paramMap, Bundle paramBundle)
    {
      return addLink(paramInt1, paramInt2, paramMap, paramBundle, null);
    }
    
    Builder addLink(int paramInt1, int paramInt2, Map<String, Float> paramMap, URLSpan paramURLSpan)
    {
      return addLink(paramInt1, paramInt2, paramMap, Bundle.EMPTY, paramURLSpan);
    }
    
    public TextLinks build()
    {
      String str = this.mFullText;
      ArrayList localArrayList = this.mLinks;
      Bundle localBundle1 = this.mExtras;
      Bundle localBundle2 = localBundle1;
      if (localBundle1 == null) {
        localBundle2 = Bundle.EMPTY;
      }
      return new TextLinks(str, localArrayList, localBundle2, null);
    }
    
    public Builder clearTextLinks()
    {
      this.mLinks.clear();
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }
  }
  
  public static final class Request
    implements Parcelable
  {
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator()
    {
      public TextLinks.Request createFromParcel(Parcel paramAnonymousParcel)
      {
        return TextLinks.Request.readFromParcel(paramAnonymousParcel);
      }
      
      public TextLinks.Request[] newArray(int paramAnonymousInt)
      {
        return new TextLinks.Request[paramAnonymousInt];
      }
    };
    private String mCallingPackageName;
    private final LocaleList mDefaultLocales;
    private final TextClassifier.EntityConfig mEntityConfig;
    private final Bundle mExtras;
    private final boolean mLegacyFallback;
    private final CharSequence mText;
    private int mUserId = 55536;
    
    private Request(CharSequence paramCharSequence, LocaleList paramLocaleList, TextClassifier.EntityConfig paramEntityConfig, boolean paramBoolean, Bundle paramBundle)
    {
      this.mText = paramCharSequence;
      this.mDefaultLocales = paramLocaleList;
      this.mEntityConfig = paramEntityConfig;
      this.mLegacyFallback = paramBoolean;
      this.mExtras = paramBundle;
    }
    
    private static Request readFromParcel(Parcel paramParcel)
    {
      String str1 = paramParcel.readString();
      LocaleList localLocaleList = (LocaleList)paramParcel.readParcelable(null);
      TextClassifier.EntityConfig localEntityConfig = (TextClassifier.EntityConfig)paramParcel.readParcelable(null);
      String str2 = paramParcel.readString();
      int i = paramParcel.readInt();
      paramParcel = new Request(str1, localLocaleList, localEntityConfig, true, paramParcel.readBundle());
      paramParcel.setCallingPackageName(str2);
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
    
    public LocaleList getDefaultLocales()
    {
      return this.mDefaultLocales;
    }
    
    public TextClassifier.EntityConfig getEntityConfig()
    {
      return this.mEntityConfig;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public int getUserId()
    {
      return this.mUserId;
    }
    
    public boolean isLegacyFallback()
    {
      return this.mLegacyFallback;
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
      paramParcel.writeString(this.mText.toString());
      paramParcel.writeParcelable(this.mDefaultLocales, paramInt);
      paramParcel.writeParcelable(this.mEntityConfig, paramInt);
      paramParcel.writeString(this.mCallingPackageName);
      paramParcel.writeInt(this.mUserId);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private LocaleList mDefaultLocales;
      private TextClassifier.EntityConfig mEntityConfig;
      private Bundle mExtras;
      private boolean mLegacyFallback = true;
      private final CharSequence mText;
      
      public Builder(CharSequence paramCharSequence)
      {
        this.mText = ((CharSequence)Preconditions.checkNotNull(paramCharSequence));
      }
      
      public TextLinks.Request build()
      {
        CharSequence localCharSequence = this.mText;
        LocaleList localLocaleList = this.mDefaultLocales;
        TextClassifier.EntityConfig localEntityConfig = this.mEntityConfig;
        boolean bool = this.mLegacyFallback;
        Bundle localBundle1 = this.mExtras;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new TextLinks.Request(localCharSequence, localLocaleList, localEntityConfig, bool, localBundle2, null);
      }
      
      public Builder setDefaultLocales(LocaleList paramLocaleList)
      {
        this.mDefaultLocales = paramLocaleList;
        return this;
      }
      
      public Builder setEntityConfig(TextClassifier.EntityConfig paramEntityConfig)
      {
        this.mEntityConfig = paramEntityConfig;
        return this;
      }
      
      public Builder setExtras(Bundle paramBundle)
      {
        this.mExtras = paramBundle;
        return this;
      }
      
      public Builder setLegacyFallback(boolean paramBoolean)
      {
        this.mLegacyFallback = paramBoolean;
        return this;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Status {}
  
  public static final class TextLink
    implements Parcelable
  {
    public static final Parcelable.Creator<TextLink> CREATOR = new Parcelable.Creator()
    {
      public TextLinks.TextLink createFromParcel(Parcel paramAnonymousParcel)
      {
        return TextLinks.TextLink.readFromParcel(paramAnonymousParcel);
      }
      
      public TextLinks.TextLink[] newArray(int paramAnonymousInt)
      {
        return new TextLinks.TextLink[paramAnonymousInt];
      }
    };
    private final int mEnd;
    private final EntityConfidence mEntityScores;
    private final Bundle mExtras;
    private final int mStart;
    private final URLSpan mUrlSpan;
    
    private TextLink(int paramInt1, int paramInt2, EntityConfidence paramEntityConfidence, Bundle paramBundle, URLSpan paramURLSpan)
    {
      Preconditions.checkNotNull(paramEntityConfidence);
      boolean bool1 = paramEntityConfidence.getEntities().isEmpty();
      boolean bool2 = true;
      Preconditions.checkArgument(bool1 ^ true);
      if (paramInt1 > paramInt2) {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      Preconditions.checkNotNull(paramBundle);
      this.mStart = paramInt1;
      this.mEnd = paramInt2;
      this.mEntityScores = paramEntityConfidence;
      this.mUrlSpan = paramURLSpan;
      this.mExtras = paramBundle;
    }
    
    private static TextLink readFromParcel(Parcel paramParcel)
    {
      EntityConfidence localEntityConfidence = (EntityConfidence)EntityConfidence.CREATOR.createFromParcel(paramParcel);
      return new TextLink(paramParcel.readInt(), paramParcel.readInt(), localEntityConfidence, paramParcel.readBundle(), null);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public float getConfidenceScore(String paramString)
    {
      return this.mEntityScores.getConfidenceScore(paramString);
    }
    
    public int getEnd()
    {
      return this.mEnd;
    }
    
    public String getEntity(int paramInt)
    {
      return (String)this.mEntityScores.getEntities().get(paramInt);
    }
    
    public int getEntityCount()
    {
      return this.mEntityScores.getEntities().size();
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public int getStart()
    {
      return this.mStart;
    }
    
    public String toString()
    {
      return String.format(Locale.US, "TextLink{start=%s, end=%s, entityScores=%s, urlSpan=%s}", new Object[] { Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), this.mEntityScores, this.mUrlSpan });
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mEntityScores.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mStart);
      paramParcel.writeInt(this.mEnd);
      paramParcel.writeBundle(this.mExtras);
    }
  }
  
  public static class TextLinkSpan
    extends ClickableSpan
  {
    public static final int INVOCATION_METHOD_KEYBOARD = 1;
    public static final int INVOCATION_METHOD_TOUCH = 0;
    public static final int INVOCATION_METHOD_UNSPECIFIED = -1;
    private final TextLinks.TextLink mTextLink;
    
    public TextLinkSpan(TextLinks.TextLink paramTextLink)
    {
      this.mTextLink = paramTextLink;
    }
    
    public final TextLinks.TextLink getTextLink()
    {
      return this.mTextLink;
    }
    
    @VisibleForTesting(visibility=VisibleForTesting.Visibility.PRIVATE)
    public final String getUrl()
    {
      if (this.mTextLink.mUrlSpan != null) {
        return this.mTextLink.mUrlSpan.getURL();
      }
      return null;
    }
    
    public void onClick(View paramView)
    {
      onClick(paramView, -1);
    }
    
    public final void onClick(View paramView, int paramInt)
    {
      if ((paramView instanceof TextView))
      {
        paramView = (TextView)paramView;
        if (TextClassificationManager.getSettings(paramView.getContext()).isSmartLinkifyEnabled())
        {
          if (paramInt != 0) {
            paramView.handleClick(this);
          } else {
            paramView.requestActionMode(this);
          }
        }
        else if (this.mTextLink.mUrlSpan != null) {
          this.mTextLink.mUrlSpan.onClick(paramView);
        } else {
          paramView.handleClick(this);
        }
      }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface InvocationMethod {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextLinks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */