package android.view.textclassifier;

import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public abstract class TextClassifierEvent
  implements Parcelable
{
  public static final int CATEGORY_CONVERSATION_ACTIONS = 3;
  public static final int CATEGORY_LANGUAGE_DETECTION = 4;
  public static final int CATEGORY_LINKIFY = 2;
  public static final int CATEGORY_SELECTION = 1;
  public static final Parcelable.Creator<TextClassifierEvent> CREATOR = new Parcelable.Creator()
  {
    public TextClassifierEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (i == 1) {
        return new TextClassifierEvent.TextSelectionEvent(paramAnonymousParcel, null);
      }
      if (i == 2) {
        return new TextClassifierEvent.TextLinkifyEvent(paramAnonymousParcel, null);
      }
      if (i == 4) {
        return new TextClassifierEvent.LanguageDetectionEvent(paramAnonymousParcel, null);
      }
      if (i == 3) {
        return new TextClassifierEvent.ConversationActionsEvent(paramAnonymousParcel, null);
      }
      throw new IllegalStateException("Unexpected input event type token in parcel.");
    }
    
    public TextClassifierEvent[] newArray(int paramAnonymousInt)
    {
      return new TextClassifierEvent[paramAnonymousInt];
    }
  };
  private static final int PARCEL_TOKEN_CONVERSATION_ACTION_EVENT = 3;
  private static final int PARCEL_TOKEN_LANGUAGE_DETECTION_EVENT = 4;
  private static final int PARCEL_TOKEN_TEXT_LINKIFY_EVENT = 2;
  private static final int PARCEL_TOKEN_TEXT_SELECTION_EVENT = 1;
  public static final int TYPE_ACTIONS_GENERATED = 20;
  public static final int TYPE_ACTIONS_SHOWN = 6;
  public static final int TYPE_AUTO_SELECTION = 5;
  public static final int TYPE_COPY_ACTION = 9;
  public static final int TYPE_CUT_ACTION = 11;
  public static final int TYPE_LINK_CLICKED = 7;
  public static final int TYPE_MANUAL_REPLY = 19;
  public static final int TYPE_OTHER_ACTION = 16;
  public static final int TYPE_OVERTYPE = 8;
  public static final int TYPE_PASTE_ACTION = 10;
  public static final int TYPE_SELECTION_DESTROYED = 15;
  public static final int TYPE_SELECTION_DRAG = 14;
  public static final int TYPE_SELECTION_MODIFIED = 2;
  public static final int TYPE_SELECTION_RESET = 18;
  public static final int TYPE_SELECTION_STARTED = 1;
  public static final int TYPE_SELECT_ALL = 17;
  public static final int TYPE_SHARE_ACTION = 12;
  public static final int TYPE_SMART_ACTION = 13;
  public static final int TYPE_SMART_SELECTION_MULTI = 4;
  public static final int TYPE_SMART_SELECTION_SINGLE = 3;
  private final int[] mActionIndices;
  private final String[] mEntityTypes;
  private final int mEventCategory;
  private TextClassificationContext mEventContext;
  private final int mEventIndex;
  private final int mEventType;
  private final Bundle mExtras;
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public TextClassificationSessionId mHiddenTempSessionId;
  private final ULocale mLocale;
  private final String mModelName;
  private final String mResultId;
  private final float[] mScores;
  
  private TextClassifierEvent(Parcel paramParcel)
  {
    this.mEventCategory = paramParcel.readInt();
    this.mEventType = paramParcel.readInt();
    this.mEntityTypes = paramParcel.readStringArray();
    ULocale localULocale = null;
    this.mEventContext = ((TextClassificationContext)paramParcel.readParcelable(null));
    this.mResultId = paramParcel.readString();
    this.mEventIndex = paramParcel.readInt();
    this.mScores = new float[paramParcel.readInt()];
    paramParcel.readFloatArray(this.mScores);
    this.mModelName = paramParcel.readString();
    this.mActionIndices = paramParcel.createIntArray();
    String str = paramParcel.readString();
    if (str != null) {
      localULocale = ULocale.forLanguageTag(str);
    }
    this.mLocale = localULocale;
    this.mExtras = paramParcel.readBundle();
  }
  
  private TextClassifierEvent(Builder paramBuilder)
  {
    this.mEventCategory = paramBuilder.mEventCategory;
    this.mEventType = paramBuilder.mEventType;
    this.mEntityTypes = paramBuilder.mEntityTypes;
    this.mEventContext = paramBuilder.mEventContext;
    this.mResultId = paramBuilder.mResultId;
    this.mEventIndex = paramBuilder.mEventIndex;
    this.mScores = paramBuilder.mScores;
    this.mModelName = paramBuilder.mModelName;
    this.mActionIndices = paramBuilder.mActionIndices;
    this.mLocale = paramBuilder.mLocale;
    if (paramBuilder.mExtras == null) {
      paramBuilder = Bundle.EMPTY;
    } else {
      paramBuilder = paramBuilder.mExtras;
    }
    this.mExtras = paramBuilder;
  }
  
  private int getParcelToken()
  {
    if ((this instanceof TextSelectionEvent)) {
      return 1;
    }
    if ((this instanceof TextLinkifyEvent)) {
      return 2;
    }
    if ((this instanceof LanguageDetectionEvent)) {
      return 4;
    }
    if ((this instanceof ConversationActionsEvent)) {
      return 3;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unexpected type: ");
    localStringBuilder.append(getClass().getSimpleName());
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int[] getActionIndices()
  {
    return this.mActionIndices;
  }
  
  public String[] getEntityTypes()
  {
    return this.mEntityTypes;
  }
  
  public int getEventCategory()
  {
    return this.mEventCategory;
  }
  
  public TextClassificationContext getEventContext()
  {
    return this.mEventContext;
  }
  
  public int getEventIndex()
  {
    return this.mEventIndex;
  }
  
  public int getEventType()
  {
    return this.mEventType;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public ULocale getLocale()
  {
    return this.mLocale;
  }
  
  public String getModelName()
  {
    return this.mModelName;
  }
  
  public String getResultId()
  {
    return this.mResultId;
  }
  
  public float[] getScores()
  {
    return this.mScores;
  }
  
  void setEventContext(TextClassificationContext paramTextClassificationContext)
  {
    this.mEventContext = paramTextClassificationContext;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public final SelectionEvent toSelectionEvent()
  {
    int i = getEventCategory();
    if (i != 1)
    {
      if (i != 2) {
        return null;
      }
      i = 2;
    }
    else
    {
      i = 1;
    }
    int j = getEntityTypes().length;
    String str = "";
    if (j > 0) {
      localObject = getEntityTypes()[0];
    } else {
      localObject = "";
    }
    SelectionEvent localSelectionEvent = new SelectionEvent(0, 0, 0, (String)localObject, 0, "");
    localSelectionEvent.setInvocationMethod(i);
    if (getEventContext() != null) {
      localSelectionEvent.setTextClassificationSessionContext(getEventContext());
    }
    localSelectionEvent.setSessionId(this.mHiddenTempSessionId);
    Object localObject = getResultId();
    if (localObject == null) {
      localObject = str;
    }
    localSelectionEvent.setResultId((String)localObject);
    localSelectionEvent.setEventIndex(getEventIndex());
    switch (getEventType())
    {
    case 6: 
    case 7: 
    default: 
      i = 0;
      break;
    case 18: 
      i = 201;
      break;
    case 17: 
      i = 200;
      break;
    case 16: 
      i = 108;
      break;
    case 15: 
      i = 107;
      break;
    case 14: 
      i = 106;
      break;
    case 13: 
      i = 105;
      break;
    case 12: 
      i = 104;
      break;
    case 11: 
      i = 103;
      break;
    case 10: 
      i = 102;
      break;
    case 9: 
      i = 101;
      break;
    case 8: 
      i = 100;
      break;
    case 5: 
      i = 5;
      break;
    case 4: 
      i = 4;
      break;
    case 3: 
      i = 3;
      break;
    case 2: 
      i = 2;
      break;
    case 1: 
      i = 1;
    }
    localSelectionEvent.setEventType(i);
    if ((this instanceof TextSelectionEvent))
    {
      localObject = (TextSelectionEvent)this;
      localSelectionEvent.setStart(((TextSelectionEvent)localObject).getRelativeWordStartIndex());
      localSelectionEvent.setEnd(((TextSelectionEvent)localObject).getRelativeWordEndIndex());
      localSelectionEvent.setSmartStart(((TextSelectionEvent)localObject).getRelativeSuggestedWordStartIndex());
      localSelectionEvent.setSmartEnd(((TextSelectionEvent)localObject).getRelativeSuggestedWordEndIndex());
    }
    return localSelectionEvent;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(getClass().getSimpleName());
    localStringBuilder.append("{");
    localStringBuilder.append("mEventCategory=");
    localStringBuilder.append(this.mEventCategory);
    localStringBuilder.append(", mEventTypes=");
    localStringBuilder.append(Arrays.toString(this.mEntityTypes));
    localStringBuilder.append(", mEventContext=");
    localStringBuilder.append(this.mEventContext);
    localStringBuilder.append(", mResultId=");
    localStringBuilder.append(this.mResultId);
    localStringBuilder.append(", mEventIndex=");
    localStringBuilder.append(this.mEventIndex);
    localStringBuilder.append(", mExtras=");
    localStringBuilder.append(this.mExtras);
    localStringBuilder.append(", mScores=");
    localStringBuilder.append(Arrays.toString(this.mScores));
    localStringBuilder.append(", mModelName=");
    localStringBuilder.append(this.mModelName);
    localStringBuilder.append(", mActionIndices=");
    localStringBuilder.append(Arrays.toString(this.mActionIndices));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(getParcelToken());
    paramParcel.writeInt(this.mEventCategory);
    paramParcel.writeInt(this.mEventType);
    paramParcel.writeStringArray(this.mEntityTypes);
    paramParcel.writeParcelable(this.mEventContext, paramInt);
    paramParcel.writeString(this.mResultId);
    paramParcel.writeInt(this.mEventIndex);
    paramParcel.writeInt(this.mScores.length);
    paramParcel.writeFloatArray(this.mScores);
    paramParcel.writeString(this.mModelName);
    paramParcel.writeIntArray(this.mActionIndices);
    Object localObject = this.mLocale;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((ULocale)localObject).toLanguageTag();
    }
    paramParcel.writeString((String)localObject);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static abstract class Builder<T extends Builder<T>>
  {
    private int[] mActionIndices = new int[0];
    private String[] mEntityTypes = new String[0];
    private final int mEventCategory;
    private TextClassificationContext mEventContext;
    private int mEventIndex;
    private final int mEventType;
    private Bundle mExtras;
    private ULocale mLocale;
    private String mModelName;
    private String mResultId;
    private float[] mScores = new float[0];
    
    private Builder(int paramInt1, int paramInt2)
    {
      this.mEventCategory = paramInt1;
      this.mEventType = paramInt2;
    }
    
    abstract T self();
    
    public T setActionIndices(int... paramVarArgs)
    {
      this.mActionIndices = new int[paramVarArgs.length];
      System.arraycopy(paramVarArgs, 0, this.mActionIndices, 0, paramVarArgs.length);
      return self();
    }
    
    public T setEntityTypes(String... paramVarArgs)
    {
      Preconditions.checkNotNull(paramVarArgs);
      this.mEntityTypes = new String[paramVarArgs.length];
      System.arraycopy(paramVarArgs, 0, this.mEntityTypes, 0, paramVarArgs.length);
      return self();
    }
    
    public T setEventContext(TextClassificationContext paramTextClassificationContext)
    {
      this.mEventContext = paramTextClassificationContext;
      return self();
    }
    
    public T setEventIndex(int paramInt)
    {
      this.mEventIndex = paramInt;
      return self();
    }
    
    public T setExtras(Bundle paramBundle)
    {
      this.mExtras = ((Bundle)Preconditions.checkNotNull(paramBundle));
      return self();
    }
    
    public T setLocale(ULocale paramULocale)
    {
      this.mLocale = paramULocale;
      return self();
    }
    
    public T setModelName(String paramString)
    {
      this.mModelName = paramString;
      return self();
    }
    
    public T setResultId(String paramString)
    {
      this.mResultId = paramString;
      return self();
    }
    
    public T setScores(float... paramVarArgs)
    {
      Preconditions.checkNotNull(paramVarArgs);
      this.mScores = new float[paramVarArgs.length];
      System.arraycopy(paramVarArgs, 0, this.mScores, 0, paramVarArgs.length);
      return self();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Category {}
  
  public static final class ConversationActionsEvent
    extends TextClassifierEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<ConversationActionsEvent> CREATOR = new Parcelable.Creator()
    {
      public TextClassifierEvent.ConversationActionsEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        paramAnonymousParcel.readInt();
        return new TextClassifierEvent.ConversationActionsEvent(paramAnonymousParcel, null);
      }
      
      public TextClassifierEvent.ConversationActionsEvent[] newArray(int paramAnonymousInt)
      {
        return new TextClassifierEvent.ConversationActionsEvent[paramAnonymousInt];
      }
    };
    
    private ConversationActionsEvent(Parcel paramParcel)
    {
      super(null);
    }
    
    private ConversationActionsEvent(Builder paramBuilder)
    {
      super(null);
    }
    
    public static final class Builder
      extends TextClassifierEvent.Builder<Builder>
    {
      public Builder(int paramInt)
      {
        super(paramInt, null);
      }
      
      public TextClassifierEvent.ConversationActionsEvent build()
      {
        return new TextClassifierEvent.ConversationActionsEvent(this, null);
      }
      
      Builder self()
      {
        return this;
      }
    }
  }
  
  public static final class LanguageDetectionEvent
    extends TextClassifierEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<LanguageDetectionEvent> CREATOR = new Parcelable.Creator()
    {
      public TextClassifierEvent.LanguageDetectionEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        paramAnonymousParcel.readInt();
        return new TextClassifierEvent.LanguageDetectionEvent(paramAnonymousParcel, null);
      }
      
      public TextClassifierEvent.LanguageDetectionEvent[] newArray(int paramAnonymousInt)
      {
        return new TextClassifierEvent.LanguageDetectionEvent[paramAnonymousInt];
      }
    };
    
    private LanguageDetectionEvent(Parcel paramParcel)
    {
      super(null);
    }
    
    private LanguageDetectionEvent(Builder paramBuilder)
    {
      super(null);
    }
    
    public static final class Builder
      extends TextClassifierEvent.Builder<Builder>
    {
      public Builder(int paramInt)
      {
        super(paramInt, null);
      }
      
      public TextClassifierEvent.LanguageDetectionEvent build()
      {
        return new TextClassifierEvent.LanguageDetectionEvent(this, null);
      }
      
      Builder self()
      {
        return this;
      }
    }
  }
  
  public static final class TextLinkifyEvent
    extends TextClassifierEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<TextLinkifyEvent> CREATOR = new Parcelable.Creator()
    {
      public TextClassifierEvent.TextLinkifyEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        paramAnonymousParcel.readInt();
        return new TextClassifierEvent.TextLinkifyEvent(paramAnonymousParcel, null);
      }
      
      public TextClassifierEvent.TextLinkifyEvent[] newArray(int paramAnonymousInt)
      {
        return new TextClassifierEvent.TextLinkifyEvent[paramAnonymousInt];
      }
    };
    
    private TextLinkifyEvent(Parcel paramParcel)
    {
      super(null);
    }
    
    private TextLinkifyEvent(Builder paramBuilder)
    {
      super(null);
    }
    
    public static final class Builder
      extends TextClassifierEvent.Builder<Builder>
    {
      public Builder(int paramInt)
      {
        super(paramInt, null);
      }
      
      public TextClassifierEvent.TextLinkifyEvent build()
      {
        return new TextClassifierEvent.TextLinkifyEvent(this, null);
      }
      
      Builder self()
      {
        return this;
      }
    }
  }
  
  public static final class TextSelectionEvent
    extends TextClassifierEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<TextSelectionEvent> CREATOR = new Parcelable.Creator()
    {
      public TextClassifierEvent.TextSelectionEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        paramAnonymousParcel.readInt();
        return new TextClassifierEvent.TextSelectionEvent(paramAnonymousParcel, null);
      }
      
      public TextClassifierEvent.TextSelectionEvent[] newArray(int paramAnonymousInt)
      {
        return new TextClassifierEvent.TextSelectionEvent[paramAnonymousInt];
      }
    };
    final int mRelativeSuggestedWordEndIndex;
    final int mRelativeSuggestedWordStartIndex;
    final int mRelativeWordEndIndex;
    final int mRelativeWordStartIndex;
    
    private TextSelectionEvent(Parcel paramParcel)
    {
      super(null);
      this.mRelativeWordStartIndex = paramParcel.readInt();
      this.mRelativeWordEndIndex = paramParcel.readInt();
      this.mRelativeSuggestedWordStartIndex = paramParcel.readInt();
      this.mRelativeSuggestedWordEndIndex = paramParcel.readInt();
    }
    
    private TextSelectionEvent(Builder paramBuilder)
    {
      super(null);
      this.mRelativeWordStartIndex = paramBuilder.mRelativeWordStartIndex;
      this.mRelativeWordEndIndex = paramBuilder.mRelativeWordEndIndex;
      this.mRelativeSuggestedWordStartIndex = paramBuilder.mRelativeSuggestedWordStartIndex;
      this.mRelativeSuggestedWordEndIndex = paramBuilder.mRelativeSuggestedWordEndIndex;
    }
    
    public int getRelativeSuggestedWordEndIndex()
    {
      return this.mRelativeSuggestedWordEndIndex;
    }
    
    public int getRelativeSuggestedWordStartIndex()
    {
      return this.mRelativeSuggestedWordStartIndex;
    }
    
    public int getRelativeWordEndIndex()
    {
      return this.mRelativeWordEndIndex;
    }
    
    public int getRelativeWordStartIndex()
    {
      return this.mRelativeWordStartIndex;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mRelativeWordStartIndex);
      paramParcel.writeInt(this.mRelativeWordEndIndex);
      paramParcel.writeInt(this.mRelativeSuggestedWordStartIndex);
      paramParcel.writeInt(this.mRelativeSuggestedWordEndIndex);
    }
    
    public static final class Builder
      extends TextClassifierEvent.Builder<Builder>
    {
      int mRelativeSuggestedWordEndIndex;
      int mRelativeSuggestedWordStartIndex;
      int mRelativeWordEndIndex;
      int mRelativeWordStartIndex;
      
      public Builder(int paramInt)
      {
        super(paramInt, null);
      }
      
      public TextClassifierEvent.TextSelectionEvent build()
      {
        return new TextClassifierEvent.TextSelectionEvent(this, null);
      }
      
      Builder self()
      {
        return this;
      }
      
      public Builder setRelativeSuggestedWordEndIndex(int paramInt)
      {
        this.mRelativeSuggestedWordEndIndex = paramInt;
        return this;
      }
      
      public Builder setRelativeSuggestedWordStartIndex(int paramInt)
      {
        this.mRelativeSuggestedWordStartIndex = paramInt;
        return this;
      }
      
      public Builder setRelativeWordEndIndex(int paramInt)
      {
        this.mRelativeWordEndIndex = paramInt;
        return this;
      }
      
      public Builder setRelativeWordStartIndex(int paramInt)
      {
        this.mRelativeWordStartIndex = paramInt;
        return this;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Type {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassifierEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */