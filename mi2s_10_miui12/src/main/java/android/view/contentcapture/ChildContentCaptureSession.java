package android.view.contentcapture;

import android.view.autofill.AutofillId;

final class ChildContentCaptureSession
  extends ContentCaptureSession
{
  private final ContentCaptureSession mParent;
  
  protected ChildContentCaptureSession(ContentCaptureSession paramContentCaptureSession, ContentCaptureContext paramContentCaptureContext)
  {
    super(paramContentCaptureContext);
    this.mParent = paramContentCaptureSession;
  }
  
  void flush(int paramInt)
  {
    this.mParent.flush(paramInt);
  }
  
  MainContentCaptureSession getMainCaptureSession()
  {
    ContentCaptureSession localContentCaptureSession = this.mParent;
    if ((localContentCaptureSession instanceof MainContentCaptureSession)) {
      return (MainContentCaptureSession)localContentCaptureSession;
    }
    return localContentCaptureSession.getMainCaptureSession();
  }
  
  void internalNotifyViewAppeared(ViewNode.ViewStructureImpl paramViewStructureImpl)
  {
    getMainCaptureSession().notifyViewAppeared(this.mId, paramViewStructureImpl);
  }
  
  void internalNotifyViewDisappeared(AutofillId paramAutofillId)
  {
    getMainCaptureSession().notifyViewDisappeared(this.mId, paramAutofillId);
  }
  
  void internalNotifyViewTextChanged(AutofillId paramAutofillId, CharSequence paramCharSequence)
  {
    getMainCaptureSession().notifyViewTextChanged(this.mId, paramAutofillId, paramCharSequence);
  }
  
  public void internalNotifyViewTreeEvent(boolean paramBoolean)
  {
    getMainCaptureSession().notifyViewTreeEvent(this.mId, paramBoolean);
  }
  
  boolean isContentCaptureEnabled()
  {
    return getMainCaptureSession().isContentCaptureEnabled();
  }
  
  ContentCaptureSession newChild(ContentCaptureContext paramContentCaptureContext)
  {
    ChildContentCaptureSession localChildContentCaptureSession = new ChildContentCaptureSession(this, paramContentCaptureContext);
    getMainCaptureSession().notifyChildSessionStarted(this.mId, localChildContentCaptureSession.mId, paramContentCaptureContext);
    return localChildContentCaptureSession;
  }
  
  void onDestroy()
  {
    getMainCaptureSession().notifyChildSessionFinished(this.mParent.mId, this.mId);
  }
  
  public void updateContentCaptureContext(ContentCaptureContext paramContentCaptureContext)
  {
    getMainCaptureSession().notifyContextUpdated(this.mId, paramContentCaptureContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ChildContentCaptureSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */