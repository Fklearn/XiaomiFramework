package android.view.textclassifier;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannedString;
import android.util.ArrayMap;
import android.view.View.OnClickListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public final class TextClassification
  implements Parcelable
{
  public static final Parcelable.Creator<TextClassification> CREATOR = new Parcelable.Creator()
  {
    public TextClassification createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextClassification(paramAnonymousParcel, null);
    }
    
    public TextClassification[] newArray(int paramAnonymousInt)
    {
      return new TextClassification[paramAnonymousInt];
    }
  };
  public static final TextClassification EMPTY = new Builder().build();
  private static final String LOG_TAG = "TextClassification";
  private static final int MAX_LEGACY_ICON_SIZE = 192;
  private final List<RemoteAction> mActions;
  private final EntityConfidence mEntityConfidence;
  private final Bundle mExtras;
  private final String mId;
  private final Drawable mLegacyIcon;
  private final Intent mLegacyIntent;
  private final String mLegacyLabel;
  private final View.OnClickListener mLegacyOnClickListener;
  private final String mText;
  
  private TextClassification(Parcel paramParcel)
  {
    this.mText = paramParcel.readString();
    this.mActions = paramParcel.createTypedArrayList(RemoteAction.CREATOR);
    if (!this.mActions.isEmpty())
    {
      RemoteAction localRemoteAction = (RemoteAction)this.mActions.get(0);
      this.mLegacyIcon = maybeLoadDrawable(localRemoteAction.getIcon());
      this.mLegacyLabel = localRemoteAction.getTitle().toString();
      this.mLegacyOnClickListener = createIntentOnClickListener(((RemoteAction)this.mActions.get(0)).getActionIntent());
    }
    else
    {
      this.mLegacyIcon = null;
      this.mLegacyLabel = null;
      this.mLegacyOnClickListener = null;
    }
    this.mLegacyIntent = null;
    this.mEntityConfidence = ((EntityConfidence)EntityConfidence.CREATOR.createFromParcel(paramParcel));
    this.mId = paramParcel.readString();
    this.mExtras = paramParcel.readBundle();
  }
  
  private TextClassification(String paramString1, Drawable paramDrawable, String paramString2, Intent paramIntent, View.OnClickListener paramOnClickListener, List<RemoteAction> paramList, EntityConfidence paramEntityConfidence, String paramString3, Bundle paramBundle)
  {
    this.mText = paramString1;
    this.mLegacyIcon = paramDrawable;
    this.mLegacyLabel = paramString2;
    this.mLegacyIntent = paramIntent;
    this.mLegacyOnClickListener = paramOnClickListener;
    this.mActions = Collections.unmodifiableList(paramList);
    this.mEntityConfidence = ((EntityConfidence)Preconditions.checkNotNull(paramEntityConfidence));
    this.mId = paramString3;
    this.mExtras = paramBundle;
  }
  
  public static View.OnClickListener createIntentOnClickListener(PendingIntent paramPendingIntent)
  {
    Preconditions.checkNotNull(paramPendingIntent);
    return new _..Lambda.TextClassification.ysasaE5ZkXkkzjVWIJ06GTV92_g(paramPendingIntent);
  }
  
  public static PendingIntent createPendingIntent(Context paramContext, Intent paramIntent, int paramInt)
  {
    return PendingIntent.getActivity(paramContext, paramInt, paramIntent, 134217728);
  }
  
  private static Drawable maybeLoadDrawable(Icon paramIcon)
  {
    if (paramIcon == null) {
      return null;
    }
    int i = paramIcon.getType();
    if (i != 1)
    {
      if (i != 3)
      {
        if (i != 5) {
          return null;
        }
        return new AdaptiveIconDrawable(null, new BitmapDrawable(Resources.getSystem(), paramIcon.getBitmap()));
      }
      return new BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(paramIcon.getDataBytes(), paramIcon.getDataOffset(), paramIcon.getDataLength()));
    }
    return new BitmapDrawable(Resources.getSystem(), paramIcon.getBitmap());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<RemoteAction> getActions()
  {
    return this.mActions;
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
  
  @Deprecated
  public Drawable getIcon()
  {
    return this.mLegacyIcon;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  @Deprecated
  public Intent getIntent()
  {
    return this.mLegacyIntent;
  }
  
  @Deprecated
  public CharSequence getLabel()
  {
    return this.mLegacyLabel;
  }
  
  public View.OnClickListener getOnClickListener()
  {
    return this.mLegacyOnClickListener;
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "TextClassification {text=%s, entities=%s, actions=%s, id=%s, extras=%s}", new Object[] { this.mText, this.mEntityConfidence, this.mActions, this.mId, this.mExtras });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mText);
    paramParcel.writeTypedList(this.mActions);
    this.mEntityConfidence.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mId);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static final class Builder
  {
    private final ArrayList<Intent> mActionIntents = new ArrayList();
    private List<RemoteAction> mActions = new ArrayList();
    private final Map<String, AnnotatorModel.ClassificationResult> mClassificationResults = new ArrayMap();
    private Bundle mExtras;
    private Bundle mForeignLanguageExtra;
    private String mId;
    private Drawable mLegacyIcon;
    private Intent mLegacyIntent;
    private String mLegacyLabel;
    private View.OnClickListener mLegacyOnClickListener;
    private String mText;
    private final Map<String, Float> mTypeScoreMap = new ArrayMap();
    
    private Bundle buildExtras(EntityConfidence paramEntityConfidence)
    {
      Object localObject1 = this.mExtras;
      Object localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = new Bundle();
      }
      if (this.mActionIntents.stream().anyMatch(_..Lambda.L_UQMPjXwBN0ch4zL2dD82nf9RI.INSTANCE)) {
        ExtrasUtils.putActionsIntents((Bundle)localObject2, this.mActionIntents);
      }
      localObject1 = this.mForeignLanguageExtra;
      if (localObject1 != null) {
        ExtrasUtils.putForeignLanguageExtra((Bundle)localObject2, (Bundle)localObject1);
      }
      localObject1 = paramEntityConfidence.getEntities();
      paramEntityConfidence = new ArrayList();
      localObject1 = ((List)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        String str = (String)((Iterator)localObject1).next();
        paramEntityConfidence.add((AnnotatorModel.ClassificationResult)this.mClassificationResults.get(str));
      }
      ExtrasUtils.putEntities((Bundle)localObject2, (AnnotatorModel.ClassificationResult[])paramEntityConfidence.toArray(new AnnotatorModel.ClassificationResult[0]));
      if (((Bundle)localObject2).isEmpty()) {
        paramEntityConfidence = Bundle.EMPTY;
      } else {
        paramEntityConfidence = (EntityConfidence)localObject2;
      }
      return paramEntityConfidence;
    }
    
    private Builder setEntityType(String paramString, float paramFloat, AnnotatorModel.ClassificationResult paramClassificationResult)
    {
      this.mTypeScoreMap.put(paramString, Float.valueOf(paramFloat));
      this.mClassificationResults.put(paramString, paramClassificationResult);
      return this;
    }
    
    public Builder addAction(RemoteAction paramRemoteAction)
    {
      return addAction(paramRemoteAction, null);
    }
    
    @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
    public Builder addAction(RemoteAction paramRemoteAction, Intent paramIntent)
    {
      boolean bool;
      if (paramRemoteAction != null) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      this.mActions.add(paramRemoteAction);
      this.mActionIntents.add(paramIntent);
      return this;
    }
    
    public TextClassification build()
    {
      EntityConfidence localEntityConfidence = new EntityConfidence(this.mTypeScoreMap);
      return new TextClassification(this.mText, this.mLegacyIcon, this.mLegacyLabel, this.mLegacyIntent, this.mLegacyOnClickListener, this.mActions, localEntityConfidence, this.mId, buildExtras(localEntityConfidence), null);
    }
    
    public Builder setEntityType(AnnotatorModel.ClassificationResult paramClassificationResult)
    {
      setEntityType(paramClassificationResult.getCollection(), paramClassificationResult.getScore(), paramClassificationResult);
      return this;
    }
    
    public Builder setEntityType(String paramString, float paramFloat)
    {
      setEntityType(paramString, paramFloat, null);
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }
    
    @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
    public Builder setForeignLanguageExtra(Bundle paramBundle)
    {
      this.mForeignLanguageExtra = paramBundle;
      return this;
    }
    
    @Deprecated
    public Builder setIcon(Drawable paramDrawable)
    {
      this.mLegacyIcon = paramDrawable;
      return this;
    }
    
    public Builder setId(String paramString)
    {
      this.mId = paramString;
      return this;
    }
    
    @Deprecated
    public Builder setIntent(Intent paramIntent)
    {
      this.mLegacyIntent = paramIntent;
      return this;
    }
    
    @Deprecated
    public Builder setLabel(String paramString)
    {
      this.mLegacyLabel = paramString;
      return this;
    }
    
    @Deprecated
    public Builder setOnClickListener(View.OnClickListener paramOnClickListener)
    {
      this.mLegacyOnClickListener = paramOnClickListener;
      return this;
    }
    
    public Builder setText(String paramString)
    {
      this.mText = paramString;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface IntentType
  {
    public static final int ACTIVITY = 0;
    public static final int SERVICE = 1;
    public static final int UNSUPPORTED = -1;
  }
  
  public static final class Request
    implements Parcelable
  {
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator()
    {
      public TextClassification.Request createFromParcel(Parcel paramAnonymousParcel)
      {
        return TextClassification.Request.readFromParcel(paramAnonymousParcel);
      }
      
      public TextClassification.Request[] newArray(int paramAnonymousInt)
      {
        return new TextClassification.Request[paramAnonymousInt];
      }
    };
    private String mCallingPackageName;
    private final LocaleList mDefaultLocales;
    private final int mEndIndex;
    private final Bundle mExtras;
    private final ZonedDateTime mReferenceTime;
    private final int mStartIndex;
    private final CharSequence mText;
    private int mUserId = 55536;
    
    private Request(CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList, ZonedDateTime paramZonedDateTime, Bundle paramBundle)
    {
      this.mText = paramCharSequence;
      this.mStartIndex = paramInt1;
      this.mEndIndex = paramInt2;
      this.mDefaultLocales = paramLocaleList;
      this.mReferenceTime = paramZonedDateTime;
      this.mExtras = paramBundle;
    }
    
    private static Request readFromParcel(Parcel paramParcel)
    {
      CharSequence localCharSequence = paramParcel.readCharSequence();
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      ZonedDateTime localZonedDateTime = null;
      LocaleList localLocaleList = (LocaleList)paramParcel.readParcelable(null);
      String str = paramParcel.readString();
      if (str != null) {
        localZonedDateTime = ZonedDateTime.parse(str);
      }
      str = paramParcel.readString();
      int k = paramParcel.readInt();
      paramParcel = new Request(localCharSequence, i, j, localLocaleList, localZonedDateTime, paramParcel.readBundle());
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
    
    public ZonedDateTime getReferenceTime()
    {
      return this.mReferenceTime;
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
      Object localObject = this.mReferenceTime;
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((ZonedDateTime)localObject).toString();
      }
      paramParcel.writeString((String)localObject);
      paramParcel.writeString(this.mCallingPackageName);
      paramParcel.writeInt(this.mUserId);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private LocaleList mDefaultLocales;
      private final int mEndIndex;
      private Bundle mExtras;
      private ZonedDateTime mReferenceTime;
      private final int mStartIndex;
      private final CharSequence mText;
      
      public Builder(CharSequence paramCharSequence, int paramInt1, int paramInt2)
      {
        TextClassifier.Utils.checkArgument(paramCharSequence, paramInt1, paramInt2);
        this.mText = paramCharSequence;
        this.mStartIndex = paramInt1;
        this.mEndIndex = paramInt2;
      }
      
      public TextClassification.Request build()
      {
        SpannedString localSpannedString = new SpannedString(this.mText);
        int i = this.mStartIndex;
        int j = this.mEndIndex;
        LocaleList localLocaleList = this.mDefaultLocales;
        ZonedDateTime localZonedDateTime = this.mReferenceTime;
        Bundle localBundle1 = this.mExtras;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new TextClassification.Request(localSpannedString, i, j, localLocaleList, localZonedDateTime, localBundle2, null);
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
      
      public Builder setReferenceTime(ZonedDateTime paramZonedDateTime)
      {
        this.mReferenceTime = paramZonedDateTime;
        return this;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */