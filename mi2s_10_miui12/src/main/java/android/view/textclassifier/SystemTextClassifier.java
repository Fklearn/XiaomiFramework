package android.view.textclassifier;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceManager.ServiceNotFoundException;
import android.service.textclassifier.ITextClassifierCallback;
import android.service.textclassifier.ITextClassifierCallback.Stub;
import android.service.textclassifier.ITextClassifierService;
import android.service.textclassifier.ITextClassifierService.Stub;
import android.service.textclassifier.TextClassifierService;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class SystemTextClassifier
  implements TextClassifier
{
  private static final String LOG_TAG = "SystemTextClassifier";
  private final TextClassifier mFallback;
  private final ITextClassifierService mManagerService = ITextClassifierService.Stub.asInterface(ServiceManager.getServiceOrThrow("textclassification"));
  private final String mPackageName;
  private TextClassificationSessionId mSessionId;
  private final TextClassificationConstants mSettings;
  private final int mUserId;
  
  public SystemTextClassifier(Context paramContext, TextClassificationConstants paramTextClassificationConstants)
    throws ServiceManager.ServiceNotFoundException
  {
    this.mSettings = ((TextClassificationConstants)Preconditions.checkNotNull(paramTextClassificationConstants));
    this.mFallback = ((TextClassificationManager)paramContext.getSystemService(TextClassificationManager.class)).getTextClassifier(0);
    this.mPackageName = ((String)Preconditions.checkNotNull(paramContext.getOpPackageName()));
    this.mUserId = paramContext.getUserId();
  }
  
  public TextClassification classifyText(TextClassification.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      paramRequest.setCallingPackageName(this.mPackageName);
      paramRequest.setUserId(this.mUserId);
      Object localObject = new android/view/textclassifier/SystemTextClassifier$BlockingCallback;
      ((BlockingCallback)localObject).<init>("textclassification");
      this.mManagerService.onClassifyText(this.mSessionId, paramRequest, (ITextClassifierCallback)localObject);
      localObject = (TextClassification)((BlockingCallback)localObject).get();
      if (localObject != null) {
        return (TextClassification)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error classifying text. Using fallback.", localRemoteException);
    }
    return this.mFallback.classifyText(paramRequest);
  }
  
  public void destroy()
  {
    try
    {
      if (this.mSessionId != null) {
        this.mManagerService.onDestroyTextClassificationSession(this.mSessionId);
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error destroying classification session.", localRemoteException);
    }
  }
  
  public TextLanguage detectLanguage(TextLanguage.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      paramRequest.setCallingPackageName(this.mPackageName);
      paramRequest.setUserId(this.mUserId);
      Object localObject = new android/view/textclassifier/SystemTextClassifier$BlockingCallback;
      ((BlockingCallback)localObject).<init>("textlanguage");
      this.mManagerService.onDetectLanguage(this.mSessionId, paramRequest, (ITextClassifierCallback)localObject);
      localObject = (TextLanguage)((BlockingCallback)localObject).get();
      if (localObject != null) {
        return (TextLanguage)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error detecting language.", localRemoteException);
    }
    return this.mFallback.detectLanguage(paramRequest);
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("SystemTextClassifier:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("mFallback", this.mFallback);
    paramIndentingPrintWriter.printPair("mPackageName", this.mPackageName);
    paramIndentingPrintWriter.printPair("mSessionId", this.mSessionId);
    paramIndentingPrintWriter.printPair("mUserId", Integer.valueOf(this.mUserId));
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println();
  }
  
  public TextLinks generateLinks(TextLinks.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    if ((!this.mSettings.isSmartLinkifyEnabled()) && (paramRequest.isLegacyFallback())) {
      return TextClassifier.Utils.generateLegacyLinks(paramRequest);
    }
    try
    {
      paramRequest.setCallingPackageName(this.mPackageName);
      paramRequest.setUserId(this.mUserId);
      Object localObject = new android/view/textclassifier/SystemTextClassifier$BlockingCallback;
      ((BlockingCallback)localObject).<init>("textlinks");
      this.mManagerService.onGenerateLinks(this.mSessionId, paramRequest, (ITextClassifierCallback)localObject);
      localObject = (TextLinks)((BlockingCallback)localObject).get();
      if (localObject != null) {
        return (TextLinks)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error generating links. Using fallback.", localRemoteException);
    }
    return this.mFallback.generateLinks(paramRequest);
  }
  
  public int getMaxGenerateLinksTextLength()
  {
    return this.mFallback.getMaxGenerateLinksTextLength();
  }
  
  void initializeRemoteSession(TextClassificationContext paramTextClassificationContext, TextClassificationSessionId paramTextClassificationSessionId)
  {
    this.mSessionId = ((TextClassificationSessionId)Preconditions.checkNotNull(paramTextClassificationSessionId));
    try
    {
      paramTextClassificationContext.setUserId(this.mUserId);
      this.mManagerService.onCreateTextClassificationSession(paramTextClassificationContext, this.mSessionId);
    }
    catch (RemoteException paramTextClassificationContext)
    {
      Log.e("SystemTextClassifier", "Error starting a new classification session.", paramTextClassificationContext);
    }
  }
  
  public void onSelectionEvent(SelectionEvent paramSelectionEvent)
  {
    Preconditions.checkNotNull(paramSelectionEvent);
    TextClassifier.Utils.checkMainThread();
    try
    {
      paramSelectionEvent.setUserId(this.mUserId);
      this.mManagerService.onSelectionEvent(this.mSessionId, paramSelectionEvent);
    }
    catch (RemoteException paramSelectionEvent)
    {
      Log.e("SystemTextClassifier", "Error reporting selection event.", paramSelectionEvent);
    }
  }
  
  public void onTextClassifierEvent(TextClassifierEvent paramTextClassifierEvent)
  {
    Preconditions.checkNotNull(paramTextClassifierEvent);
    TextClassifier.Utils.checkMainThread();
    try
    {
      Object localObject;
      if (paramTextClassifierEvent.getEventContext() == null)
      {
        localObject = new android/view/textclassifier/TextClassificationContext$Builder;
        ((TextClassificationContext.Builder)localObject).<init>(this.mPackageName, "unknown");
        localObject = ((TextClassificationContext.Builder)localObject).build();
      }
      else
      {
        localObject = paramTextClassifierEvent.getEventContext();
      }
      ((TextClassificationContext)localObject).setUserId(this.mUserId);
      paramTextClassifierEvent.setEventContext((TextClassificationContext)localObject);
      this.mManagerService.onTextClassifierEvent(this.mSessionId, paramTextClassifierEvent);
    }
    catch (RemoteException paramTextClassifierEvent)
    {
      Log.e("SystemTextClassifier", "Error reporting textclassifier event.", paramTextClassifierEvent);
    }
  }
  
  public ConversationActions suggestConversationActions(ConversationActions.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      paramRequest.setCallingPackageName(this.mPackageName);
      paramRequest.setUserId(this.mUserId);
      Object localObject = new android/view/textclassifier/SystemTextClassifier$BlockingCallback;
      ((BlockingCallback)localObject).<init>("conversation-actions");
      this.mManagerService.onSuggestConversationActions(this.mSessionId, paramRequest, (ITextClassifierCallback)localObject);
      localObject = (ConversationActions)((BlockingCallback)localObject).get();
      if (localObject != null) {
        return (ConversationActions)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error reporting selection event.", localRemoteException);
    }
    return this.mFallback.suggestConversationActions(paramRequest);
  }
  
  public TextSelection suggestSelection(TextSelection.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      paramRequest.setCallingPackageName(this.mPackageName);
      paramRequest.setUserId(this.mUserId);
      Object localObject = new android/view/textclassifier/SystemTextClassifier$BlockingCallback;
      ((BlockingCallback)localObject).<init>("textselection");
      this.mManagerService.onSuggestSelection(this.mSessionId, paramRequest, (ITextClassifierCallback)localObject);
      localObject = (TextSelection)((BlockingCallback)localObject).get();
      if (localObject != null) {
        return (TextSelection)localObject;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemTextClassifier", "Error suggesting selection for text. Using fallback.", localRemoteException);
    }
    return this.mFallback.suggestSelection(paramRequest);
  }
  
  private static final class BlockingCallback<T extends Parcelable>
    extends ITextClassifierCallback.Stub
  {
    private final SystemTextClassifier.ResponseReceiver<T> mReceiver;
    
    BlockingCallback(String paramString)
    {
      this.mReceiver = new SystemTextClassifier.ResponseReceiver(paramString, null);
    }
    
    public T get()
    {
      return (Parcelable)this.mReceiver.get();
    }
    
    public void onFailure()
    {
      this.mReceiver.onFailure();
    }
    
    public void onSuccess(Bundle paramBundle)
    {
      this.mReceiver.onSuccess(TextClassifierService.getResponse(paramBundle));
    }
  }
  
  private static final class ResponseReceiver<T>
  {
    private final CountDownLatch mLatch = new CountDownLatch(1);
    private final String mName;
    private T mResponse;
    
    private ResponseReceiver(String paramString)
    {
      this.mName = paramString;
    }
    
    public T get()
    {
      if (Looper.myLooper() != Looper.getMainLooper()) {
        try
        {
          if (!this.mLatch.await(2L, TimeUnit.SECONDS))
          {
            StringBuilder localStringBuilder1 = new java/lang/StringBuilder;
            localStringBuilder1.<init>();
            localStringBuilder1.append("Timeout in ResponseReceiver.get(): ");
            localStringBuilder1.append(this.mName);
            Log.w("SystemTextClassifier", localStringBuilder1.toString());
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
          StringBuilder localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append("Interrupted during ResponseReceiver.get(): ");
          localStringBuilder2.append(this.mName);
          Log.e("SystemTextClassifier", localStringBuilder2.toString(), localInterruptedException);
        }
      }
      return (T)this.mResponse;
    }
    
    public void onFailure()
    {
      Log.e("SystemTextClassifier", "Request failed.", null);
      this.mLatch.countDown();
    }
    
    public void onSuccess(T paramT)
    {
      this.mResponse = paramT;
      this.mLatch.countDown();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/SystemTextClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */