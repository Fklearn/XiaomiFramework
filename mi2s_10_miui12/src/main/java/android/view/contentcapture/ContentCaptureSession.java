package android.view.contentcapture;

import android.util.DebugUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Random;

public abstract class ContentCaptureSession
  implements AutoCloseable
{
  public static final int FLUSH_REASON_FULL = 1;
  public static final int FLUSH_REASON_IDLE_TIMEOUT = 5;
  public static final int FLUSH_REASON_SESSION_FINISHED = 4;
  public static final int FLUSH_REASON_SESSION_STARTED = 3;
  public static final int FLUSH_REASON_TEXT_CHANGE_TIMEOUT = 6;
  public static final int FLUSH_REASON_VIEW_ROOT_ENTERED = 2;
  private static final int INITIAL_CHILDREN_CAPACITY = 5;
  public static final int NO_SESSION_ID = 0;
  public static final int STATE_ACTIVE = 2;
  public static final int STATE_BY_APP = 64;
  public static final int STATE_DISABLED = 4;
  public static final int STATE_DUPLICATED_ID = 8;
  public static final int STATE_FLAG_SECURE = 32;
  public static final int STATE_INTERNAL_ERROR = 256;
  public static final int STATE_NOT_WHITELISTED = 512;
  public static final int STATE_NO_RESPONSE = 128;
  public static final int STATE_NO_SERVICE = 16;
  public static final int STATE_SERVICE_DIED = 1024;
  public static final int STATE_SERVICE_RESURRECTED = 4096;
  public static final int STATE_SERVICE_UPDATING = 2048;
  public static final int STATE_WAITING_FOR_SERVER = 1;
  private static final String TAG = ContentCaptureSession.class.getSimpleName();
  public static final int UNKNOWN_STATE = 0;
  private static final Random sIdGenerator = new Random();
  @GuardedBy({"mLock"})
  private ArrayList<ContentCaptureSession> mChildren;
  private ContentCaptureContext mClientContext;
  private ContentCaptureSessionId mContentCaptureSessionId;
  @GuardedBy({"mLock"})
  private boolean mDestroyed;
  protected final int mId;
  private final Object mLock = new Object();
  private int mState;
  
  protected ContentCaptureSession()
  {
    this(getRandomSessionId());
  }
  
  @VisibleForTesting
  public ContentCaptureSession(int paramInt)
  {
    boolean bool = false;
    this.mState = 0;
    if (paramInt != 0) {
      bool = true;
    }
    Preconditions.checkArgument(bool);
    this.mId = paramInt;
  }
  
  ContentCaptureSession(ContentCaptureContext paramContentCaptureContext)
  {
    this();
    this.mClientContext = ((ContentCaptureContext)Preconditions.checkNotNull(paramContentCaptureContext));
  }
  
  public static String getFlushReasonAsString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UNKOWN-");
      localStringBuilder.append(paramInt);
      return localStringBuilder.toString();
    case 6: 
      return "TEXT_CHANGE";
    case 5: 
      return "IDLE";
    case 4: 
      return "FINISHED";
    case 3: 
      return "STARTED";
    case 2: 
      return "VIEW_ROOT";
    }
    return "FULL";
  }
  
  private static int getRandomSessionId()
  {
    for (;;)
    {
      int i = sIdGenerator.nextInt();
      if (i != 0) {
        return i;
      }
    }
  }
  
  protected static String getStateAsString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" (");
    String str;
    if (paramInt == 0) {
      str = "UNKNOWN";
    } else {
      str = DebugUtils.flagsToString(ContentCaptureSession.class, "STATE_", paramInt);
    }
    localStringBuilder.append(str);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void close()
  {
    destroy();
  }
  
  public final ContentCaptureSession createContentCaptureSession(ContentCaptureContext arg1)
  {
    ContentCaptureSession localContentCaptureSession = newChild(???);
    Object localObject2;
    if (ContentCaptureHelper.sDebug)
    {
      localObject2 = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("createContentCaptureSession(");
      localStringBuilder.append(???);
      localStringBuilder.append(": parent=");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(", child=");
      localStringBuilder.append(localContentCaptureSession.mId);
      Log.d((String)localObject2, localStringBuilder.toString());
    }
    synchronized (this.mLock)
    {
      if (this.mChildren == null)
      {
        localObject2 = new java/util/ArrayList;
        ((ArrayList)localObject2).<init>(5);
        this.mChildren = ((ArrayList)localObject2);
      }
      this.mChildren.add(localContentCaptureSession);
      return localContentCaptureSession;
    }
  }
  
  public final void destroy()
  {
    synchronized (this.mLock)
    {
      String str;
      Object localObject3;
      if (this.mDestroyed)
      {
        if (ContentCaptureHelper.sDebug)
        {
          str = TAG;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((StringBuilder)localObject3).append("destroy(");
          ((StringBuilder)localObject3).append(this.mId);
          ((StringBuilder)localObject3).append("): already destroyed");
          Log.d(str, ((StringBuilder)localObject3).toString());
        }
        return;
      }
      this.mDestroyed = true;
      if (ContentCaptureHelper.sVerbose)
      {
        str = TAG;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((StringBuilder)localObject3).append("destroy(): state=");
        ((StringBuilder)localObject3).append(getStateAsString(this.mState));
        ((StringBuilder)localObject3).append(", mId=");
        ((StringBuilder)localObject3).append(this.mId);
        Log.v(str, ((StringBuilder)localObject3).toString());
      }
      if (this.mChildren != null)
      {
        int i = this.mChildren.size();
        if (ContentCaptureHelper.sVerbose)
        {
          str = TAG;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((StringBuilder)localObject3).append("Destroying ");
          ((StringBuilder)localObject3).append(i);
          ((StringBuilder)localObject3).append(" children first");
          Log.v(str, ((StringBuilder)localObject3).toString());
        }
        for (int j = 0; j < i; j++)
        {
          localObject3 = (ContentCaptureSession)this.mChildren.get(j);
          try
          {
            ((ContentCaptureSession)localObject3).destroy();
          }
          catch (Exception localException)
          {
            localObject3 = TAG;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("exception destroying child session #");
            localStringBuilder.append(j);
            localStringBuilder.append(": ");
            localStringBuilder.append(localException);
            Log.w((String)localObject3, localStringBuilder.toString());
          }
        }
      }
      try
      {
        flush(4);
        return;
      }
      finally
      {
        onDestroy();
      }
    }
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("id: ");
    paramPrintWriter.println(this.mId);
    if (this.mClientContext != null)
    {
      paramPrintWriter.print(paramString);
      this.mClientContext.dump(paramPrintWriter);
      paramPrintWriter.println();
    }
    synchronized (this.mLock)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("destroyed: ");
      paramPrintWriter.println(this.mDestroyed);
      if ((this.mChildren != null) && (!this.mChildren.isEmpty()))
      {
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append(paramString);
        ((StringBuilder)localObject2).append("  ");
        String str = ((StringBuilder)localObject2).toString();
        int i = this.mChildren.size();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("number children: ");
        paramPrintWriter.println(i);
        for (int j = 0; j < i; j++)
        {
          localObject2 = (ContentCaptureSession)this.mChildren.get(j);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print(j);
          paramPrintWriter.println(": ");
          ((ContentCaptureSession)localObject2).dump(str, paramPrintWriter);
        }
      }
      return;
    }
  }
  
  abstract void flush(int paramInt);
  
  public final ContentCaptureContext getContentCaptureContext()
  {
    return this.mClientContext;
  }
  
  public final ContentCaptureSessionId getContentCaptureSessionId()
  {
    if (this.mContentCaptureSessionId == null) {
      this.mContentCaptureSessionId = new ContentCaptureSessionId(this.mId);
    }
    return this.mContentCaptureSessionId;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  abstract MainContentCaptureSession getMainCaptureSession();
  
  abstract void internalNotifyViewAppeared(ViewNode.ViewStructureImpl paramViewStructureImpl);
  
  abstract void internalNotifyViewDisappeared(AutofillId paramAutofillId);
  
  abstract void internalNotifyViewTextChanged(AutofillId paramAutofillId, CharSequence paramCharSequence);
  
  public abstract void internalNotifyViewTreeEvent(boolean paramBoolean);
  
  boolean isContentCaptureEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool;
      if (!this.mDestroyed) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public AutofillId newAutofillId(AutofillId paramAutofillId, long paramLong)
  {
    Preconditions.checkNotNull(paramAutofillId);
    Preconditions.checkArgument(paramAutofillId.isNonVirtual(), "hostId cannot be virtual: %s", new Object[] { paramAutofillId });
    return new AutofillId(paramAutofillId, paramLong, this.mId);
  }
  
  abstract ContentCaptureSession newChild(ContentCaptureContext paramContentCaptureContext);
  
  public final ViewStructure newViewStructure(View paramView)
  {
    return new ViewNode.ViewStructureImpl(paramView);
  }
  
  public final ViewStructure newVirtualViewStructure(AutofillId paramAutofillId, long paramLong)
  {
    return new ViewNode.ViewStructureImpl(paramAutofillId, paramLong, this.mId);
  }
  
  public final void notifyViewAppeared(ViewStructure paramViewStructure)
  {
    Preconditions.checkNotNull(paramViewStructure);
    if (!isContentCaptureEnabled()) {
      return;
    }
    if ((paramViewStructure instanceof ViewNode.ViewStructureImpl))
    {
      internalNotifyViewAppeared((ViewNode.ViewStructureImpl)paramViewStructure);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid node class: ");
    localStringBuilder.append(paramViewStructure.getClass());
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public final void notifyViewDisappeared(AutofillId paramAutofillId)
  {
    Preconditions.checkNotNull(paramAutofillId);
    if (!isContentCaptureEnabled()) {
      return;
    }
    internalNotifyViewDisappeared(paramAutofillId);
  }
  
  public final void notifyViewTextChanged(AutofillId paramAutofillId, CharSequence paramCharSequence)
  {
    Preconditions.checkNotNull(paramAutofillId);
    if (!isContentCaptureEnabled()) {
      return;
    }
    internalNotifyViewTextChanged(paramAutofillId, paramCharSequence);
  }
  
  public final void notifyViewsDisappeared(AutofillId paramAutofillId, long[] paramArrayOfLong)
  {
    boolean bool = paramAutofillId.isNonVirtual();
    int i = 0;
    Preconditions.checkArgument(bool, "hostId cannot be virtual: %s", new Object[] { paramAutofillId });
    Preconditions.checkArgument(ArrayUtils.isEmpty(paramArrayOfLong) ^ true, "virtual ids cannot be empty");
    if (!isContentCaptureEnabled()) {
      return;
    }
    int j = paramArrayOfLong.length;
    while (i < j)
    {
      internalNotifyViewDisappeared(new AutofillId(paramAutofillId, paramArrayOfLong[i], this.mId));
      i++;
    }
  }
  
  abstract void onDestroy();
  
  public final void setContentCaptureContext(ContentCaptureContext paramContentCaptureContext)
  {
    this.mClientContext = paramContentCaptureContext;
    updateContentCaptureContext(paramContentCaptureContext);
  }
  
  public String toString()
  {
    return Integer.toString(this.mId);
  }
  
  abstract void updateContentCaptureContext(ContentCaptureContext paramContentCaptureContext);
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FlushReason {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */