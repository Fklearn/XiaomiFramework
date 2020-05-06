package android.view.contentcapture;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.autofill.AutofillId;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@SystemApi
public final class ContentCaptureEvent
  implements Parcelable
{
  public static final Parcelable.Creator<ContentCaptureEvent> CREATOR = new Parcelable.Creator()
  {
    public ContentCaptureEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      ContentCaptureEvent localContentCaptureEvent = new ContentCaptureEvent(i, j, paramAnonymousParcel.readLong());
      Object localObject = (AutofillId)paramAnonymousParcel.readParcelable(null);
      if (localObject != null) {
        localContentCaptureEvent.setAutofillId((AutofillId)localObject);
      }
      localObject = paramAnonymousParcel.createTypedArrayList(AutofillId.CREATOR);
      if (localObject != null) {
        localContentCaptureEvent.setAutofillIds((ArrayList)localObject);
      }
      localObject = ViewNode.readFromParcel(paramAnonymousParcel);
      if (localObject != null) {
        localContentCaptureEvent.setViewNode((ViewNode)localObject);
      }
      localContentCaptureEvent.setText(paramAnonymousParcel.readCharSequence());
      if ((j == -1) || (j == -2)) {
        localContentCaptureEvent.setParentSessionId(paramAnonymousParcel.readInt());
      }
      if ((j == -1) || (j == 6)) {
        localContentCaptureEvent.setClientContext((ContentCaptureContext)paramAnonymousParcel.readParcelable(null));
      }
      return localContentCaptureEvent;
    }
    
    public ContentCaptureEvent[] newArray(int paramAnonymousInt)
    {
      return new ContentCaptureEvent[paramAnonymousInt];
    }
  };
  private static final String TAG = ContentCaptureEvent.class.getSimpleName();
  public static final int TYPE_CONTEXT_UPDATED = 6;
  public static final int TYPE_SESSION_FINISHED = -2;
  public static final int TYPE_SESSION_PAUSED = 8;
  public static final int TYPE_SESSION_RESUMED = 7;
  public static final int TYPE_SESSION_STARTED = -1;
  public static final int TYPE_VIEW_APPEARED = 1;
  public static final int TYPE_VIEW_DISAPPEARED = 2;
  public static final int TYPE_VIEW_TEXT_CHANGED = 3;
  public static final int TYPE_VIEW_TREE_APPEARED = 5;
  public static final int TYPE_VIEW_TREE_APPEARING = 4;
  private ContentCaptureContext mClientContext;
  private final long mEventTime;
  private AutofillId mId;
  private ArrayList<AutofillId> mIds;
  private ViewNode mNode;
  private int mParentSessionId = 0;
  private final int mSessionId;
  private CharSequence mText;
  private final int mType;
  
  public ContentCaptureEvent(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, System.currentTimeMillis());
  }
  
  public ContentCaptureEvent(int paramInt1, int paramInt2, long paramLong)
  {
    this.mSessionId = paramInt1;
    this.mType = paramInt2;
    this.mEventTime = paramLong;
  }
  
  public static String getTypeAsString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UKNOWN_TYPE: ");
      localStringBuilder.append(paramInt);
      return localStringBuilder.toString();
    case 8: 
      return "SESSION_PAUSED";
    case 7: 
      return "SESSION_RESUMED";
    case 6: 
      return "CONTEXT_UPDATED";
    case 5: 
      return "VIEW_TREE_APPEARED";
    case 4: 
      return "VIEW_TREE_APPEARING";
    case 3: 
      return "VIEW_TEXT_CHANGED";
    case 2: 
      return "VIEW_DISAPPEARED";
    case 1: 
      return "VIEW_APPEARED";
    case -1: 
      return "SESSION_STARTED";
    }
    return "SESSION_FINISHED";
  }
  
  public ContentCaptureEvent addAutofillId(AutofillId paramAutofillId)
  {
    Preconditions.checkNotNull(paramAutofillId);
    if (this.mIds == null)
    {
      this.mIds = new ArrayList();
      Object localObject = this.mId;
      if (localObject == null)
      {
        localObject = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("addAutofillId(");
        localStringBuilder.append(paramAutofillId);
        localStringBuilder.append(") called without an initial id");
        Log.w((String)localObject, localStringBuilder.toString());
      }
      else
      {
        this.mIds.add(localObject);
        this.mId = null;
      }
    }
    this.mIds.add(paramAutofillId);
    return this;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("type=");
    paramPrintWriter.print(getTypeAsString(this.mType));
    paramPrintWriter.print(", time=");
    paramPrintWriter.print(this.mEventTime);
    if (this.mId != null)
    {
      paramPrintWriter.print(", id=");
      paramPrintWriter.print(this.mId);
    }
    if (this.mIds != null)
    {
      paramPrintWriter.print(", ids=");
      paramPrintWriter.print(this.mIds);
    }
    if (this.mNode != null)
    {
      paramPrintWriter.print(", mNode.id=");
      paramPrintWriter.print(this.mNode.getAutofillId());
    }
    if (this.mSessionId != 0)
    {
      paramPrintWriter.print(", sessionId=");
      paramPrintWriter.print(this.mSessionId);
    }
    if (this.mParentSessionId != 0)
    {
      paramPrintWriter.print(", parentSessionId=");
      paramPrintWriter.print(this.mParentSessionId);
    }
    if (this.mText != null)
    {
      paramPrintWriter.print(", text=");
      paramPrintWriter.println(ContentCaptureHelper.getSanitizedString(this.mText));
    }
    if (this.mClientContext != null)
    {
      paramPrintWriter.print(", context=");
      this.mClientContext.dump(paramPrintWriter);
      paramPrintWriter.println();
    }
  }
  
  public ContentCaptureContext getContentCaptureContext()
  {
    return this.mClientContext;
  }
  
  public long getEventTime()
  {
    return this.mEventTime;
  }
  
  public AutofillId getId()
  {
    return this.mId;
  }
  
  public List<AutofillId> getIds()
  {
    return this.mIds;
  }
  
  public int getParentSessionId()
  {
    return this.mParentSessionId;
  }
  
  public int getSessionId()
  {
    return this.mSessionId;
  }
  
  public CharSequence getText()
  {
    return this.mText;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public ViewNode getViewNode()
  {
    return this.mNode;
  }
  
  public void mergeEvent(ContentCaptureEvent paramContentCaptureEvent)
  {
    Preconditions.checkNotNull(paramContentCaptureEvent);
    int i = paramContentCaptureEvent.getType();
    Object localObject1;
    if (this.mType != i)
    {
      paramContentCaptureEvent = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("mergeEvent(");
      ((StringBuilder)localObject1).append(getTypeAsString(i));
      ((StringBuilder)localObject1).append(") cannot be merged with different eventType=");
      ((StringBuilder)localObject1).append(getTypeAsString(this.mType));
      Log.e(paramContentCaptureEvent, ((StringBuilder)localObject1).toString());
      return;
    }
    if (i == 2)
    {
      localObject1 = paramContentCaptureEvent.getIds();
      Object localObject2 = paramContentCaptureEvent.getId();
      if (localObject1 != null)
      {
        if (localObject2 != null)
        {
          String str = TAG;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("got TYPE_VIEW_DISAPPEARED event with both id and ids: ");
          ((StringBuilder)localObject2).append(paramContentCaptureEvent);
          Log.w(str, ((StringBuilder)localObject2).toString());
        }
        for (i = 0; i < ((List)localObject1).size(); i++) {
          addAutofillId((AutofillId)((List)localObject1).get(i));
        }
        return;
      }
      if (localObject2 != null)
      {
        addAutofillId((AutofillId)localObject2);
        return;
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("mergeEvent(): got TYPE_VIEW_DISAPPEARED event with neither id or ids: ");
      ((StringBuilder)localObject1).append(paramContentCaptureEvent);
      throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
    }
    if (i == 3)
    {
      setText(paramContentCaptureEvent.getText());
    }
    else
    {
      paramContentCaptureEvent = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("mergeEvent(");
      ((StringBuilder)localObject1).append(getTypeAsString(i));
      ((StringBuilder)localObject1).append(") does not support this event type.");
      Log.e(paramContentCaptureEvent, ((StringBuilder)localObject1).toString());
    }
  }
  
  public ContentCaptureEvent setAutofillId(AutofillId paramAutofillId)
  {
    this.mId = ((AutofillId)Preconditions.checkNotNull(paramAutofillId));
    return this;
  }
  
  public ContentCaptureEvent setAutofillIds(ArrayList<AutofillId> paramArrayList)
  {
    this.mIds = ((ArrayList)Preconditions.checkNotNull(paramArrayList));
    return this;
  }
  
  public ContentCaptureEvent setClientContext(ContentCaptureContext paramContentCaptureContext)
  {
    this.mClientContext = paramContentCaptureContext;
    return this;
  }
  
  public ContentCaptureEvent setParentSessionId(int paramInt)
  {
    this.mParentSessionId = paramInt;
    return this;
  }
  
  public ContentCaptureEvent setText(CharSequence paramCharSequence)
  {
    this.mText = paramCharSequence;
    return this;
  }
  
  public ContentCaptureEvent setViewNode(ViewNode paramViewNode)
  {
    this.mNode = ((ViewNode)Preconditions.checkNotNull(paramViewNode));
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ContentCaptureEvent[type=").append(getTypeAsString(this.mType));
    localStringBuilder.append(", session=");
    localStringBuilder.append(this.mSessionId);
    if ((this.mType == -1) && (this.mParentSessionId != 0))
    {
      localStringBuilder.append(", parent=");
      localStringBuilder.append(this.mParentSessionId);
    }
    if (this.mId != null)
    {
      localStringBuilder.append(", id=");
      localStringBuilder.append(this.mId);
    }
    if (this.mIds != null)
    {
      localStringBuilder.append(", ids=");
      localStringBuilder.append(this.mIds);
    }
    Object localObject = this.mNode;
    if (localObject != null)
    {
      localObject = ((ViewNode)localObject).getClassName();
      if (this.mNode != null)
      {
        localStringBuilder.append(", class=");
        localStringBuilder.append((String)localObject);
      }
      localStringBuilder.append(", id=");
      localStringBuilder.append(this.mNode.getAutofillId());
    }
    if (this.mText != null)
    {
      localStringBuilder.append(", text=");
      localStringBuilder.append(ContentCaptureHelper.getSanitizedString(this.mText));
    }
    if (this.mClientContext != null)
    {
      localStringBuilder.append(", context=");
      localStringBuilder.append(this.mClientContext);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSessionId);
    paramParcel.writeInt(this.mType);
    paramParcel.writeLong(this.mEventTime);
    paramParcel.writeParcelable(this.mId, paramInt);
    paramParcel.writeTypedList(this.mIds);
    ViewNode.writeToParcel(paramParcel, this.mNode, paramInt);
    paramParcel.writeCharSequence(this.mText);
    int i = this.mType;
    if ((i == -1) || (i == -2)) {
      paramParcel.writeInt(this.mParentSessionId);
    }
    i = this.mType;
    if ((i == -1) || (i == 6)) {
      paramParcel.writeParcelable(this.mClientContext, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EventType {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */