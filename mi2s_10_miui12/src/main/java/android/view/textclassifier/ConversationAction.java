package android.view.textclassifier;

import android.app.RemoteAction;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ConversationAction
  implements Parcelable
{
  public static final Parcelable.Creator<ConversationAction> CREATOR = new Parcelable.Creator()
  {
    public ConversationAction createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConversationAction(paramAnonymousParcel, null);
    }
    
    public ConversationAction[] newArray(int paramAnonymousInt)
    {
      return new ConversationAction[paramAnonymousInt];
    }
  };
  public static final String TYPE_ADD_CONTACT = "add_contact";
  public static final String TYPE_CALL_PHONE = "call_phone";
  public static final String TYPE_COPY = "copy";
  public static final String TYPE_CREATE_REMINDER = "create_reminder";
  public static final String TYPE_OPEN_URL = "open_url";
  public static final String TYPE_SEND_EMAIL = "send_email";
  public static final String TYPE_SEND_SMS = "send_sms";
  public static final String TYPE_SHARE_LOCATION = "share_location";
  public static final String TYPE_TEXT_REPLY = "text_reply";
  public static final String TYPE_TRACK_FLIGHT = "track_flight";
  public static final String TYPE_VIEW_CALENDAR = "view_calendar";
  public static final String TYPE_VIEW_MAP = "view_map";
  private final RemoteAction mAction;
  private final Bundle mExtras;
  private final float mScore;
  private final CharSequence mTextReply;
  private final String mType;
  
  private ConversationAction(Parcel paramParcel)
  {
    this.mType = paramParcel.readString();
    this.mAction = ((RemoteAction)paramParcel.readParcelable(null));
    this.mTextReply = paramParcel.readCharSequence();
    this.mScore = paramParcel.readFloat();
    this.mExtras = paramParcel.readBundle();
  }
  
  private ConversationAction(String paramString, RemoteAction paramRemoteAction, CharSequence paramCharSequence, float paramFloat, Bundle paramBundle)
  {
    this.mType = ((String)Preconditions.checkNotNull(paramString));
    this.mAction = paramRemoteAction;
    this.mTextReply = paramCharSequence;
    this.mScore = paramFloat;
    this.mExtras = ((Bundle)Preconditions.checkNotNull(paramBundle));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public RemoteAction getAction()
  {
    return this.mAction;
  }
  
  public float getConfidenceScore()
  {
    return this.mScore;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public CharSequence getTextReply()
  {
    return this.mTextReply;
  }
  
  public String getType()
  {
    return this.mType;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mType);
    paramParcel.writeParcelable(this.mAction, paramInt);
    paramParcel.writeCharSequence(this.mTextReply);
    paramParcel.writeFloat(this.mScore);
    paramParcel.writeBundle(this.mExtras);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ActionType {}
  
  public static final class Builder
  {
    private RemoteAction mAction;
    private Bundle mExtras;
    private float mScore;
    private CharSequence mTextReply;
    private String mType;
    
    public Builder(String paramString)
    {
      this.mType = ((String)Preconditions.checkNotNull(paramString));
    }
    
    public ConversationAction build()
    {
      String str = this.mType;
      RemoteAction localRemoteAction = this.mAction;
      CharSequence localCharSequence = this.mTextReply;
      float f = this.mScore;
      Bundle localBundle1 = this.mExtras;
      Bundle localBundle2 = localBundle1;
      if (localBundle1 == null) {
        localBundle2 = Bundle.EMPTY;
      }
      return new ConversationAction(str, localRemoteAction, localCharSequence, f, localBundle2, null);
    }
    
    public Builder setAction(RemoteAction paramRemoteAction)
    {
      this.mAction = paramRemoteAction;
      return this;
    }
    
    public Builder setConfidenceScore(float paramFloat)
    {
      this.mScore = paramFloat;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }
    
    public Builder setTextReply(CharSequence paramCharSequence)
    {
      this.mTextReply = paramCharSequence;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ConversationAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */