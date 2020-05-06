package android.view.textclassifier;

import com.android.internal.util.Preconditions;

final class TextClassificationSession
  implements TextClassifier
{
  private static final String LOG_TAG = "TextClassificationSession";
  private final TextClassificationContext mClassificationContext;
  private final TextClassifier mDelegate;
  private boolean mDestroyed;
  private final SelectionEventHelper mEventHelper;
  private final TextClassificationSessionId mSessionId;
  
  TextClassificationSession(TextClassificationContext paramTextClassificationContext, TextClassifier paramTextClassifier)
  {
    this.mClassificationContext = ((TextClassificationContext)Preconditions.checkNotNull(paramTextClassificationContext));
    this.mDelegate = ((TextClassifier)Preconditions.checkNotNull(paramTextClassifier));
    this.mSessionId = new TextClassificationSessionId();
    this.mEventHelper = new SelectionEventHelper(this.mSessionId, this.mClassificationContext);
    initializeRemoteSession();
  }
  
  private void checkDestroyed()
  {
    if (!this.mDestroyed) {
      return;
    }
    throw new IllegalStateException("This TextClassification session has been destroyed");
  }
  
  private void initializeRemoteSession()
  {
    TextClassifier localTextClassifier = this.mDelegate;
    if ((localTextClassifier instanceof SystemTextClassifier)) {
      ((SystemTextClassifier)localTextClassifier).initializeRemoteSession(this.mClassificationContext, this.mSessionId);
    }
  }
  
  public TextClassification classifyText(TextClassification.Request paramRequest)
  {
    checkDestroyed();
    return this.mDelegate.classifyText(paramRequest);
  }
  
  public void destroy()
  {
    this.mEventHelper.endSession();
    this.mDelegate.destroy();
    this.mDestroyed = true;
  }
  
  public TextLinks generateLinks(TextLinks.Request paramRequest)
  {
    checkDestroyed();
    return this.mDelegate.generateLinks(paramRequest);
  }
  
  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  public void onSelectionEvent(SelectionEvent paramSelectionEvent)
  {
    try
    {
      if (this.mEventHelper.sanitizeEvent(paramSelectionEvent)) {
        this.mDelegate.onSelectionEvent(paramSelectionEvent);
      }
    }
    catch (Exception paramSelectionEvent)
    {
      Log.e("TextClassificationSession", "Error reporting text classifier selection event", paramSelectionEvent);
    }
  }
  
  public void onTextClassifierEvent(TextClassifierEvent paramTextClassifierEvent)
  {
    try
    {
      paramTextClassifierEvent.mHiddenTempSessionId = this.mSessionId;
      this.mDelegate.onTextClassifierEvent(paramTextClassifierEvent);
    }
    catch (Exception paramTextClassifierEvent)
    {
      Log.e("TextClassificationSession", "Error reporting text classifier event", paramTextClassifierEvent);
    }
  }
  
  public TextSelection suggestSelection(TextSelection.Request paramRequest)
  {
    checkDestroyed();
    return this.mDelegate.suggestSelection(paramRequest);
  }
  
  private static final class SelectionEventHelper
  {
    private final TextClassificationContext mContext;
    private int mInvocationMethod = 0;
    private SelectionEvent mPrevEvent;
    private final TextClassificationSessionId mSessionId;
    private SelectionEvent mSmartEvent;
    private SelectionEvent mStartEvent;
    
    SelectionEventHelper(TextClassificationSessionId paramTextClassificationSessionId, TextClassificationContext paramTextClassificationContext)
    {
      this.mSessionId = ((TextClassificationSessionId)Preconditions.checkNotNull(paramTextClassificationSessionId));
      this.mContext = ((TextClassificationContext)Preconditions.checkNotNull(paramTextClassificationContext));
    }
    
    private void modifyAutoSelectionEventType(SelectionEvent paramSelectionEvent)
    {
      int i = paramSelectionEvent.getEventType();
      if ((i != 3) && (i != 4) && (i != 5)) {
        return;
      }
      if (SelectionSessionLogger.isPlatformLocalTextClassifierSmartSelection(paramSelectionEvent.getResultId()))
      {
        if (paramSelectionEvent.getAbsoluteEnd() - paramSelectionEvent.getAbsoluteStart() > 1) {
          paramSelectionEvent.setEventType(4);
        } else {
          paramSelectionEvent.setEventType(3);
        }
      }
      else {
        paramSelectionEvent.setEventType(5);
      }
    }
    
    private void updateInvocationMethod(SelectionEvent paramSelectionEvent)
    {
      paramSelectionEvent.setTextClassificationSessionContext(this.mContext);
      if (paramSelectionEvent.getInvocationMethod() == 0) {
        paramSelectionEvent.setInvocationMethod(this.mInvocationMethod);
      } else {
        this.mInvocationMethod = paramSelectionEvent.getInvocationMethod();
      }
    }
    
    void endSession()
    {
      this.mPrevEvent = null;
      this.mSmartEvent = null;
      this.mStartEvent = null;
    }
    
    boolean sanitizeEvent(SelectionEvent paramSelectionEvent)
    {
      updateInvocationMethod(paramSelectionEvent);
      modifyAutoSelectionEventType(paramSelectionEvent);
      int i = paramSelectionEvent.getEventType();
      boolean bool = false;
      if ((i != 1) && (this.mStartEvent == null))
      {
        Log.d("TextClassificationSession", "Selection session not yet started. Ignoring event");
        return false;
      }
      long l = System.currentTimeMillis();
      i = paramSelectionEvent.getEventType();
      if (i != 1)
      {
        if (i != 2)
        {
          if ((i == 3) || (i == 4) || (i == 5)) {
            this.mSmartEvent = paramSelectionEvent;
          }
        }
        else
        {
          localSelectionEvent = this.mPrevEvent;
          if ((localSelectionEvent != null) && (localSelectionEvent.getAbsoluteStart() == paramSelectionEvent.getAbsoluteStart()) && (this.mPrevEvent.getAbsoluteEnd() == paramSelectionEvent.getAbsoluteEnd())) {
            return false;
          }
        }
      }
      else
      {
        if (paramSelectionEvent.getAbsoluteEnd() == paramSelectionEvent.getAbsoluteStart() + 1) {
          bool = true;
        }
        Preconditions.checkArgument(bool);
        paramSelectionEvent.setSessionId(this.mSessionId);
        this.mStartEvent = paramSelectionEvent;
      }
      paramSelectionEvent.setEventTime(l);
      SelectionEvent localSelectionEvent = this.mStartEvent;
      if (localSelectionEvent != null) {
        paramSelectionEvent.setSessionId(localSelectionEvent.getSessionId()).setDurationSinceSessionStart(l - this.mStartEvent.getEventTime()).setStart(paramSelectionEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setEnd(paramSelectionEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
      }
      localSelectionEvent = this.mSmartEvent;
      if (localSelectionEvent != null) {
        paramSelectionEvent.setResultId(localSelectionEvent.getResultId()).setSmartStart(this.mSmartEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setSmartEnd(this.mSmartEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
      }
      localSelectionEvent = this.mPrevEvent;
      if (localSelectionEvent != null) {
        paramSelectionEvent.setDurationSincePreviousEvent(l - localSelectionEvent.getEventTime()).setEventIndex(this.mPrevEvent.getEventIndex() + 1);
      }
      this.mPrevEvent = paramSelectionEvent;
      return true;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassificationSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */