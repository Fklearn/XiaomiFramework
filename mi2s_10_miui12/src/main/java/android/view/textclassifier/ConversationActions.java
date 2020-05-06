package android.view.textclassifier;

import android.app.Person;
import android.app.Person.Builder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannedString;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConversationActions
  implements Parcelable
{
  public static final Parcelable.Creator<ConversationActions> CREATOR = new Parcelable.Creator()
  {
    public ConversationActions createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConversationActions(paramAnonymousParcel, null);
    }
    
    public ConversationActions[] newArray(int paramAnonymousInt)
    {
      return new ConversationActions[paramAnonymousInt];
    }
  };
  private final List<ConversationAction> mConversationActions;
  private final String mId;
  
  private ConversationActions(Parcel paramParcel)
  {
    this.mConversationActions = Collections.unmodifiableList(paramParcel.createTypedArrayList(ConversationAction.CREATOR));
    this.mId = paramParcel.readString();
  }
  
  public ConversationActions(List<ConversationAction> paramList, String paramString)
  {
    this.mConversationActions = Collections.unmodifiableList((List)Preconditions.checkNotNull(paramList));
    this.mId = paramString;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<ConversationAction> getConversationActions()
  {
    return this.mConversationActions;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedList(this.mConversationActions);
    paramParcel.writeString(this.mId);
  }
  
  public static final class Message
    implements Parcelable
  {
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator()
    {
      public ConversationActions.Message createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ConversationActions.Message(paramAnonymousParcel, null);
      }
      
      public ConversationActions.Message[] newArray(int paramAnonymousInt)
      {
        return new ConversationActions.Message[paramAnonymousInt];
      }
    };
    public static final Person PERSON_USER_OTHERS;
    public static final Person PERSON_USER_SELF = new Person.Builder().setKey("text-classifier-conversation-actions-user-self").build();
    private final Person mAuthor;
    private final Bundle mExtras;
    private final ZonedDateTime mReferenceTime;
    private final CharSequence mText;
    
    static
    {
      PERSON_USER_OTHERS = new Person.Builder().setKey("text-classifier-conversation-actions-user-others").build();
    }
    
    private Message(Person paramPerson, ZonedDateTime paramZonedDateTime, CharSequence paramCharSequence, Bundle paramBundle)
    {
      this.mAuthor = paramPerson;
      this.mReferenceTime = paramZonedDateTime;
      this.mText = paramCharSequence;
      this.mExtras = ((Bundle)Preconditions.checkNotNull(paramBundle));
    }
    
    private Message(Parcel paramParcel)
    {
      ZonedDateTime localZonedDateTime = null;
      this.mAuthor = ((Person)paramParcel.readParcelable(null));
      if (paramParcel.readInt() != 0) {
        localZonedDateTime = ZonedDateTime.parse(paramParcel.readString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
      }
      this.mReferenceTime = localZonedDateTime;
      this.mText = paramParcel.readCharSequence();
      this.mExtras = paramParcel.readBundle();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public Person getAuthor()
    {
      return this.mAuthor;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public ZonedDateTime getReferenceTime()
    {
      return this.mReferenceTime;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeParcelable(this.mAuthor, paramInt);
      if (this.mReferenceTime != null) {
        paramInt = 1;
      } else {
        paramInt = 0;
      }
      paramParcel.writeInt(paramInt);
      ZonedDateTime localZonedDateTime = this.mReferenceTime;
      if (localZonedDateTime != null) {
        paramParcel.writeString(localZonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
      }
      paramParcel.writeCharSequence(this.mText);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private Person mAuthor;
      private Bundle mExtras;
      private ZonedDateTime mReferenceTime;
      private CharSequence mText;
      
      public Builder(Person paramPerson)
      {
        this.mAuthor = ((Person)Preconditions.checkNotNull(paramPerson));
      }
      
      public ConversationActions.Message build()
      {
        Person localPerson = this.mAuthor;
        ZonedDateTime localZonedDateTime = this.mReferenceTime;
        Object localObject = this.mText;
        if (localObject == null) {
          localObject = null;
        } else {
          localObject = new SpannedString((CharSequence)localObject);
        }
        Bundle localBundle1 = this.mExtras;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new ConversationActions.Message(localPerson, localZonedDateTime, (CharSequence)localObject, localBundle2, null);
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
      
      public Builder setText(CharSequence paramCharSequence)
      {
        this.mText = paramCharSequence;
        return this;
      }
    }
  }
  
  public static final class Request
    implements Parcelable
  {
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator()
    {
      public ConversationActions.Request createFromParcel(Parcel paramAnonymousParcel)
      {
        return ConversationActions.Request.readFromParcel(paramAnonymousParcel);
      }
      
      public ConversationActions.Request[] newArray(int paramAnonymousInt)
      {
        return new ConversationActions.Request[paramAnonymousInt];
      }
    };
    public static final String HINT_FOR_IN_APP = "in_app";
    public static final String HINT_FOR_NOTIFICATION = "notification";
    private String mCallingPackageName;
    private final List<ConversationActions.Message> mConversation;
    private Bundle mExtras;
    private final List<String> mHints;
    private final int mMaxSuggestions;
    private final TextClassifier.EntityConfig mTypeConfig;
    private int mUserId = 55536;
    
    private Request(List<ConversationActions.Message> paramList, TextClassifier.EntityConfig paramEntityConfig, int paramInt, List<String> paramList1, Bundle paramBundle)
    {
      this.mConversation = ((List)Preconditions.checkNotNull(paramList));
      this.mTypeConfig = ((TextClassifier.EntityConfig)Preconditions.checkNotNull(paramEntityConfig));
      this.mMaxSuggestions = paramInt;
      this.mHints = paramList1;
      this.mExtras = paramBundle;
    }
    
    private static Request readFromParcel(Parcel paramParcel)
    {
      ArrayList localArrayList1 = new ArrayList();
      paramParcel.readParcelableList(localArrayList1, null);
      TextClassifier.EntityConfig localEntityConfig = (TextClassifier.EntityConfig)paramParcel.readParcelable(null);
      int i = paramParcel.readInt();
      ArrayList localArrayList2 = new ArrayList();
      paramParcel.readStringList(localArrayList2);
      String str = paramParcel.readString();
      int j = paramParcel.readInt();
      paramParcel = new Request(localArrayList1, localEntityConfig, i, localArrayList2, paramParcel.readBundle());
      paramParcel.setCallingPackageName(str);
      paramParcel.setUserId(j);
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
    
    public List<ConversationActions.Message> getConversation()
    {
      return this.mConversation;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public List<String> getHints()
    {
      return this.mHints;
    }
    
    public int getMaxSuggestions()
    {
      return this.mMaxSuggestions;
    }
    
    public TextClassifier.EntityConfig getTypeConfig()
    {
      return this.mTypeConfig;
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
      paramParcel.writeParcelableList(this.mConversation, paramInt);
      paramParcel.writeParcelable(this.mTypeConfig, paramInt);
      paramParcel.writeInt(this.mMaxSuggestions);
      paramParcel.writeStringList(this.mHints);
      paramParcel.writeString(this.mCallingPackageName);
      paramParcel.writeInt(this.mUserId);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private List<ConversationActions.Message> mConversation;
      private Bundle mExtras;
      private List<String> mHints;
      private int mMaxSuggestions = -1;
      private TextClassifier.EntityConfig mTypeConfig;
      
      public Builder(List<ConversationActions.Message> paramList)
      {
        this.mConversation = ((List)Preconditions.checkNotNull(paramList));
      }
      
      public ConversationActions.Request build()
      {
        List localList1 = Collections.unmodifiableList(this.mConversation);
        TextClassifier.EntityConfig localEntityConfig = this.mTypeConfig;
        if (localEntityConfig == null) {
          localEntityConfig = new TextClassifier.EntityConfig.Builder().build();
        }
        int i = this.mMaxSuggestions;
        List localList2 = this.mHints;
        if (localList2 == null) {
          localList2 = Collections.emptyList();
        } else {
          localList2 = Collections.unmodifiableList(localList2);
        }
        Bundle localBundle1 = this.mExtras;
        Bundle localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = Bundle.EMPTY;
        }
        return new ConversationActions.Request(localList1, localEntityConfig, i, localList2, localBundle2, null);
      }
      
      public Builder setExtras(Bundle paramBundle)
      {
        this.mExtras = paramBundle;
        return this;
      }
      
      public Builder setHints(List<String> paramList)
      {
        this.mHints = paramList;
        return this;
      }
      
      public Builder setMaxSuggestions(int paramInt)
      {
        this.mMaxSuggestions = Preconditions.checkArgumentNonnegative(paramInt);
        return this;
      }
      
      public Builder setTypeConfig(TextClassifier.EntityConfig paramEntityConfig)
      {
        this.mTypeConfig = paramEntityConfig;
        return this;
      }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Hint {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ConversationActions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */