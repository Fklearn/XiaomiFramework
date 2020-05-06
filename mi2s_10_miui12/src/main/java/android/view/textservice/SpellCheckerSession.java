package android.view.textservice;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ISpellCheckerSessionListener.Stub;
import com.android.internal.textservice.ITextServicesSessionListener;
import com.android.internal.textservice.ITextServicesSessionListener.Stub;
import dalvik.system.CloseGuard;
import java.util.LinkedList;
import java.util.Queue;

public class SpellCheckerSession
{
  private static final boolean DBG = false;
  private static final int MSG_ON_GET_SUGGESTION_MULTIPLE = 1;
  private static final int MSG_ON_GET_SUGGESTION_MULTIPLE_FOR_SENTENCE = 2;
  public static final String SERVICE_META_DATA = "android.view.textservice.scs";
  private static final String TAG = SpellCheckerSession.class.getSimpleName();
  private final CloseGuard mGuard = CloseGuard.get();
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i = paramAnonymousMessage.what;
      if (i != 1)
      {
        if (i == 2) {
          SpellCheckerSession.this.handleOnGetSentenceSuggestionsMultiple((SentenceSuggestionsInfo[])paramAnonymousMessage.obj);
        }
      }
      else {
        SpellCheckerSession.this.handleOnGetSuggestionsMultiple((SuggestionsInfo[])paramAnonymousMessage.obj);
      }
    }
  };
  private final InternalListener mInternalListener;
  private final SpellCheckerInfo mSpellCheckerInfo;
  @UnsupportedAppUsage
  private final SpellCheckerSessionListener mSpellCheckerSessionListener;
  private final SpellCheckerSessionListenerImpl mSpellCheckerSessionListenerImpl;
  private final TextServicesManager mTextServicesManager;
  
  public SpellCheckerSession(SpellCheckerInfo paramSpellCheckerInfo, TextServicesManager paramTextServicesManager, SpellCheckerSessionListener paramSpellCheckerSessionListener)
  {
    if ((paramSpellCheckerInfo != null) && (paramSpellCheckerSessionListener != null) && (paramTextServicesManager != null))
    {
      this.mSpellCheckerInfo = paramSpellCheckerInfo;
      this.mSpellCheckerSessionListenerImpl = new SpellCheckerSessionListenerImpl(this.mHandler);
      this.mInternalListener = new InternalListener(this.mSpellCheckerSessionListenerImpl);
      this.mTextServicesManager = paramTextServicesManager;
      this.mSpellCheckerSessionListener = paramSpellCheckerSessionListener;
      this.mGuard.open("finishSession");
      return;
    }
    throw new NullPointerException();
  }
  
  private void handleOnGetSentenceSuggestionsMultiple(SentenceSuggestionsInfo[] paramArrayOfSentenceSuggestionsInfo)
  {
    this.mSpellCheckerSessionListener.onGetSentenceSuggestions(paramArrayOfSentenceSuggestionsInfo);
  }
  
  private void handleOnGetSuggestionsMultiple(SuggestionsInfo[] paramArrayOfSuggestionsInfo)
  {
    this.mSpellCheckerSessionListener.onGetSuggestions(paramArrayOfSuggestionsInfo);
  }
  
  public void cancel()
  {
    this.mSpellCheckerSessionListenerImpl.cancel();
  }
  
  public void close()
  {
    this.mGuard.close();
    this.mSpellCheckerSessionListenerImpl.close();
    this.mTextServicesManager.finishSpellCheckerService(this.mSpellCheckerSessionListenerImpl);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mGuard != null)
      {
        this.mGuard.warnIfOpen();
        close();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void getSentenceSuggestions(TextInfo[] paramArrayOfTextInfo, int paramInt)
  {
    this.mSpellCheckerSessionListenerImpl.getSentenceSuggestionsMultiple(paramArrayOfTextInfo, paramInt);
  }
  
  public SpellCheckerInfo getSpellChecker()
  {
    return this.mSpellCheckerInfo;
  }
  
  public ISpellCheckerSessionListener getSpellCheckerSessionListener()
  {
    return this.mSpellCheckerSessionListenerImpl;
  }
  
  @Deprecated
  public void getSuggestions(TextInfo paramTextInfo, int paramInt)
  {
    getSuggestions(new TextInfo[] { paramTextInfo }, paramInt, false);
  }
  
  @Deprecated
  public void getSuggestions(TextInfo[] paramArrayOfTextInfo, int paramInt, boolean paramBoolean)
  {
    this.mSpellCheckerSessionListenerImpl.getSuggestionsMultiple(paramArrayOfTextInfo, paramInt, paramBoolean);
  }
  
  public ITextServicesSessionListener getTextServicesSessionListener()
  {
    return this.mInternalListener;
  }
  
  public boolean isSessionDisconnected()
  {
    return this.mSpellCheckerSessionListenerImpl.isDisconnected();
  }
  
  private static final class InternalListener
    extends ITextServicesSessionListener.Stub
  {
    private final SpellCheckerSession.SpellCheckerSessionListenerImpl mParentSpellCheckerSessionListenerImpl;
    
    public InternalListener(SpellCheckerSession.SpellCheckerSessionListenerImpl paramSpellCheckerSessionListenerImpl)
    {
      this.mParentSpellCheckerSessionListenerImpl = paramSpellCheckerSessionListenerImpl;
    }
    
    public void onServiceConnected(ISpellCheckerSession paramISpellCheckerSession)
    {
      this.mParentSpellCheckerSessionListenerImpl.onServiceConnected(paramISpellCheckerSession);
    }
  }
  
  public static abstract interface SpellCheckerSessionListener
  {
    public abstract void onGetSentenceSuggestions(SentenceSuggestionsInfo[] paramArrayOfSentenceSuggestionsInfo);
    
    public abstract void onGetSuggestions(SuggestionsInfo[] paramArrayOfSuggestionsInfo);
  }
  
  private static final class SpellCheckerSessionListenerImpl
    extends ISpellCheckerSessionListener.Stub
  {
    private static final int STATE_CLOSED_AFTER_CONNECTION = 2;
    private static final int STATE_CLOSED_BEFORE_CONNECTION = 3;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_WAIT_CONNECTION = 0;
    private static final int TASK_CANCEL = 1;
    private static final int TASK_CLOSE = 3;
    private static final int TASK_GET_SUGGESTIONS_MULTIPLE = 2;
    private static final int TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE = 4;
    private Handler mAsyncHandler;
    private Handler mHandler;
    private ISpellCheckerSession mISpellCheckerSession;
    private final Queue<SpellCheckerParams> mPendingTasks = new LinkedList();
    private int mState = 0;
    private HandlerThread mThread;
    
    public SpellCheckerSessionListenerImpl(Handler paramHandler)
    {
      this.mHandler = paramHandler;
    }
    
    private void processCloseLocked()
    {
      this.mISpellCheckerSession = null;
      Object localObject = this.mThread;
      if (localObject != null) {
        ((HandlerThread)localObject).quit();
      }
      this.mHandler = null;
      this.mPendingTasks.clear();
      this.mThread = null;
      this.mAsyncHandler = null;
      int i = this.mState;
      if (i != 0)
      {
        if (i != 1)
        {
          String str = SpellCheckerSession.TAG;
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("processCloseLocked is called unexpectedly. mState=");
          ((StringBuilder)localObject).append(stateToString(this.mState));
          Log.e(str, ((StringBuilder)localObject).toString());
        }
        else
        {
          this.mState = 2;
        }
      }
      else {
        this.mState = 3;
      }
    }
    
    private void processOrEnqueueTask(SpellCheckerParams paramSpellCheckerParams)
    {
      try
      {
        if ((paramSpellCheckerParams.mWhat == 3) && ((this.mState == 2) || (this.mState == 3))) {
          return;
        }
        Object localObject2;
        if ((this.mState != 0) && (this.mState != 1))
        {
          localObject1 = SpellCheckerSession.TAG;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          ((StringBuilder)localObject2).append("ignoring processOrEnqueueTask due to unexpected mState=");
          ((StringBuilder)localObject2).append(stateToString(this.mState));
          ((StringBuilder)localObject2).append(" scp.mWhat=");
          ((StringBuilder)localObject2).append(taskToString(paramSpellCheckerParams.mWhat));
          Log.e((String)localObject1, ((StringBuilder)localObject2).toString());
          return;
        }
        if (this.mState == 0)
        {
          if (paramSpellCheckerParams.mWhat == 3)
          {
            processCloseLocked();
            return;
          }
          localObject2 = null;
          localObject1 = null;
          if (paramSpellCheckerParams.mWhat == 1) {
            for (;;)
            {
              localObject2 = localObject1;
              if (this.mPendingTasks.isEmpty()) {
                break;
              }
              localObject2 = (SpellCheckerParams)this.mPendingTasks.poll();
              if (((SpellCheckerParams)localObject2).mWhat == 3) {
                localObject1 = localObject2;
              }
            }
          }
          this.mPendingTasks.offer(paramSpellCheckerParams);
          if (localObject2 != null) {
            this.mPendingTasks.offer(localObject2);
          }
          return;
        }
        Object localObject1 = this.mISpellCheckerSession;
        processTask((ISpellCheckerSession)localObject1, paramSpellCheckerParams, false);
        return;
      }
      finally {}
    }
    
    private void processTask(ISpellCheckerSession paramISpellCheckerSession, SpellCheckerParams paramSpellCheckerParams, boolean paramBoolean)
    {
      if (!paramBoolean)
      {
        Handler localHandler = this.mAsyncHandler;
        if (localHandler != null)
        {
          paramSpellCheckerParams.mSession = paramISpellCheckerSession;
          localHandler.sendMessage(Message.obtain(localHandler, 1, paramSpellCheckerParams));
          break label297;
        }
      }
      int i = paramSpellCheckerParams.mWhat;
      Object localObject;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i == 4) {
              try
              {
                paramISpellCheckerSession.onGetSentenceSuggestionsMultiple(paramSpellCheckerParams.mTextInfos, paramSpellCheckerParams.mSuggestionsLimit);
              }
              catch (RemoteException localRemoteException1)
              {
                paramISpellCheckerSession = SpellCheckerSession.TAG;
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append("Failed to get suggestions ");
                ((StringBuilder)localObject).append(localRemoteException1);
                Log.e(paramISpellCheckerSession, ((StringBuilder)localObject).toString());
              }
            }
          }
          else {
            try
            {
              paramISpellCheckerSession.onClose();
            }
            catch (RemoteException localRemoteException2)
            {
              localObject = SpellCheckerSession.TAG;
              paramISpellCheckerSession = new StringBuilder();
              paramISpellCheckerSession.append("Failed to close ");
              paramISpellCheckerSession.append(localRemoteException2);
              Log.e((String)localObject, paramISpellCheckerSession.toString());
            }
          }
        }
        else {
          try
          {
            paramISpellCheckerSession.onGetSuggestionsMultiple(paramSpellCheckerParams.mTextInfos, paramSpellCheckerParams.mSuggestionsLimit, paramSpellCheckerParams.mSequentialWords);
          }
          catch (RemoteException localRemoteException3)
          {
            localObject = SpellCheckerSession.TAG;
            paramISpellCheckerSession = new StringBuilder();
            paramISpellCheckerSession.append("Failed to get suggestions ");
            paramISpellCheckerSession.append(localRemoteException3);
            Log.e((String)localObject, paramISpellCheckerSession.toString());
          }
        }
      }
      else {
        try
        {
          paramISpellCheckerSession.onCancel();
        }
        catch (RemoteException localRemoteException4)
        {
          paramISpellCheckerSession = SpellCheckerSession.TAG;
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Failed to cancel ");
          ((StringBuilder)localObject).append(localRemoteException4);
          Log.e(paramISpellCheckerSession, ((StringBuilder)localObject).toString());
        }
      }
      label297:
      if (paramSpellCheckerParams.mWhat == 3) {
        try
        {
          processCloseLocked();
        }
        finally {}
      }
    }
    
    private static String stateToString(int paramInt)
    {
      if (paramInt != 0)
      {
        if (paramInt != 1)
        {
          if (paramInt != 2)
          {
            if (paramInt != 3)
            {
              StringBuilder localStringBuilder = new StringBuilder();
              localStringBuilder.append("Unexpected state=");
              localStringBuilder.append(paramInt);
              return localStringBuilder.toString();
            }
            return "STATE_CLOSED_BEFORE_CONNECTION";
          }
          return "STATE_CLOSED_AFTER_CONNECTION";
        }
        return "STATE_CONNECTED";
      }
      return "STATE_WAIT_CONNECTION";
    }
    
    private static String taskToString(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3)
          {
            if (paramInt != 4)
            {
              StringBuilder localStringBuilder = new StringBuilder();
              localStringBuilder.append("Unexpected task=");
              localStringBuilder.append(paramInt);
              return localStringBuilder.toString();
            }
            return "TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE";
          }
          return "TASK_CLOSE";
        }
        return "TASK_GET_SUGGESTIONS_MULTIPLE";
      }
      return "TASK_CANCEL";
    }
    
    public void cancel()
    {
      processOrEnqueueTask(new SpellCheckerParams(1, null, 0, false));
    }
    
    public void close()
    {
      processOrEnqueueTask(new SpellCheckerParams(3, null, 0, false));
    }
    
    public void getSentenceSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt)
    {
      processOrEnqueueTask(new SpellCheckerParams(4, paramArrayOfTextInfo, paramInt, false));
    }
    
    public void getSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt, boolean paramBoolean)
    {
      processOrEnqueueTask(new SpellCheckerParams(2, paramArrayOfTextInfo, paramInt, paramBoolean));
    }
    
    public boolean isDisconnected()
    {
      try
      {
        int i = this.mState;
        boolean bool = true;
        if (i == 1) {
          bool = false;
        }
        return bool;
      }
      finally {}
    }
    
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] paramArrayOfSentenceSuggestionsInfo)
    {
      try
      {
        if (this.mHandler != null) {
          this.mHandler.sendMessage(Message.obtain(this.mHandler, 2, paramArrayOfSentenceSuggestionsInfo));
        }
        return;
      }
      finally {}
    }
    
    public void onGetSuggestions(SuggestionsInfo[] paramArrayOfSuggestionsInfo)
    {
      try
      {
        if (this.mHandler != null) {
          this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, paramArrayOfSuggestionsInfo));
        }
        return;
      }
      finally {}
    }
    
    public void onServiceConnected(ISpellCheckerSession paramISpellCheckerSession)
    {
      try
      {
        int i = this.mState;
        Object localObject;
        if (i != 0)
        {
          if (i != 3)
          {
            paramISpellCheckerSession = SpellCheckerSession.TAG;
            localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            ((StringBuilder)localObject).append("ignoring onServiceConnected due to unexpected mState=");
            ((StringBuilder)localObject).append(stateToString(this.mState));
            Log.e(paramISpellCheckerSession, ((StringBuilder)localObject).toString());
            return;
          }
          return;
        }
        if (paramISpellCheckerSession == null)
        {
          Log.e(SpellCheckerSession.TAG, "ignoring onServiceConnected due to session=null");
          return;
        }
        this.mISpellCheckerSession = paramISpellCheckerSession;
        if (((paramISpellCheckerSession.asBinder() instanceof Binder)) && (this.mThread == null))
        {
          localObject = new android/os/HandlerThread;
          ((HandlerThread)localObject).<init>("SpellCheckerSession", 10);
          this.mThread = ((HandlerThread)localObject);
          this.mThread.start();
          localObject = new android/view/textservice/SpellCheckerSession$SpellCheckerSessionListenerImpl$1;
          ((1)localObject).<init>(this, this.mThread.getLooper());
          this.mAsyncHandler = ((Handler)localObject);
        }
        this.mState = 1;
        while (!this.mPendingTasks.isEmpty()) {
          processTask(paramISpellCheckerSession, (SpellCheckerParams)this.mPendingTasks.poll(), false);
        }
        return;
      }
      finally {}
    }
    
    private static class SpellCheckerParams
    {
      public final boolean mSequentialWords;
      public ISpellCheckerSession mSession;
      public final int mSuggestionsLimit;
      public final TextInfo[] mTextInfos;
      public final int mWhat;
      
      public SpellCheckerParams(int paramInt1, TextInfo[] paramArrayOfTextInfo, int paramInt2, boolean paramBoolean)
      {
        this.mWhat = paramInt1;
        this.mTextInfos = paramArrayOfTextInfo;
        this.mSuggestionsLimit = paramInt2;
        this.mSequentialWords = paramBoolean;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/SpellCheckerSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */