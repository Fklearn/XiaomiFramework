package android.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.miui.translationservice.ITranslation;
import com.miui.translationservice.ITranslation.Stub;
import com.miui.translationservice.ITranslationRemoteCallback.Stub;
import com.miui.translationservice.provider.TranslationResult;
import miui.os.Build;

class TranslationManager
{
  static final int MSG_QUERY_FAIL = 1;
  static final int MSG_QUERY_SUCCESS = 0;
  private static final int STATE_DESTROYING = 3;
  private static final int STATE_ERROR = -1;
  private static final int STATE_INIT = 0;
  private static final int STATE_INITIALIZING = 1;
  private static final int STATE_TRANSLATING = 2;
  private static final String TAG = "TranslationManager";
  private static final String TRANSLATION_SERVICE_CLASS = "com.miui.translationservice.TranslationService";
  private static final String TRANSLATION_SERVICE_PACKAGE = "com.miui.translationservice";
  private ServiceConnection mConnection;
  private Context mContext;
  private Handler mHandler;
  private Handler mMainHandler = new Handler(Looper.getMainLooper());
  private TranslateTask mPendingTask;
  private ITranslation mService = null;
  private int mState = 0;
  
  TranslationManager(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mHandler = paramHandler;
  }
  
  private void deliverResult(TranslationResult paramTranslationResult)
  {
    this.mHandler.removeCallbacksAndMessages(null);
    if (paramTranslationResult == null) {
      paramTranslationResult = this.mHandler.obtainMessage(1);
    } else {
      paramTranslationResult = this.mHandler.obtainMessage(0, paramTranslationResult);
    }
    this.mHandler.sendMessageDelayed(paramTranslationResult, 200L);
  }
  
  private void doDestroy()
  {
    this.mState = 3;
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        TranslationManager.this.mContext.unbindService(TranslationManager.this.mConnection);
        return null;
      }
      
      protected void onPostExecute(Void paramAnonymousVoid)
      {
        TranslationManager.this.onDestroyed();
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private void doInitialize()
  {
    Log.d("TranslationManager", "try to bind translation service");
    this.mState = 1;
    if (this.mConnection == null) {
      this.mConnection = new TranslationConnection(null);
    }
    new AsyncTask()
    {
      protected Boolean doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new Intent().setClassName("com.miui.translationservice", "com.miui.translationservice.TranslationService");
        return Boolean.valueOf(TranslationManager.this.mContext.bindService(paramAnonymousVarArgs, TranslationManager.this.mConnection, 1));
      }
      
      protected void onPostExecute(Boolean paramAnonymousBoolean)
      {
        if ((paramAnonymousBoolean == null) || (!paramAnonymousBoolean.booleanValue())) {
          TranslationManager.this.onInitialized(null);
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private void doTranslate()
  {
    this.mState = 2;
    try
    {
      ITranslation localITranslation = this.mService;
      String str1 = this.mPendingTask.mSource;
      String str2 = this.mPendingTask.mTarget;
      String str3 = this.mPendingTask.mWord;
      TranslationRemoteCallback localTranslationRemoteCallback = new android/widget/TranslationManager$TranslationRemoteCallback;
      localTranslationRemoteCallback.<init>(this, this.mPendingTask);
      localITranslation.translate(str1, str2, str3, localTranslationRemoteCallback);
    }
    catch (RemoteException localRemoteException)
    {
      Log.i("TranslationManager", "bind translation service failed", localRemoteException);
      onTranslateDone(this.mPendingTask, null);
    }
  }
  
  private void onDestroyed()
  {
    this.mService = null;
    this.mConnection = null;
    this.mState = 0;
    if (this.mPendingTask != null)
    {
      Log.d("TranslationManager", "new task received when destroying");
      doInitialize();
    }
  }
  
  private void onDisconnected()
  {
    if (this.mState == 2)
    {
      Log.d("TranslationManager", "disconnected during translating");
      this.mService = null;
      deliverResult(null);
      this.mPendingTask = null;
      doDestroy();
    }
  }
  
  private void onInitialized(ITranslation paramITranslation)
  {
    if (paramITranslation != null)
    {
      this.mService = paramITranslation;
      if (this.mPendingTask != null)
      {
        Log.d("TranslationManager", "translate pending task");
        doTranslate();
      }
      else
      {
        Log.d("TranslationManager", "no pending task, unbind service directly");
        doDestroy();
      }
    }
    else
    {
      Log.i("TranslationManager", "bind service failed");
      this.mConnection = null;
      this.mState = -1;
      if (this.mPendingTask != null)
      {
        this.mPendingTask = null;
        deliverResult(null);
      }
    }
  }
  
  private void onTranslateDone(TranslateTask paramTranslateTask, TranslationResult paramTranslationResult)
  {
    if (TranslateTask.equals(this.mPendingTask, paramTranslateTask))
    {
      Log.d("TranslationManager", "translate task done");
      deliverResult(paramTranslationResult);
      this.mPendingTask = null;
    }
    if (this.mState == 2) {
      if (this.mPendingTask == null)
      {
        Log.d("TranslationManager", "no pending task found. release service");
        doDestroy();
      }
      else
      {
        Log.d("TranslationManager", "task changed");
        doTranslate();
      }
    }
  }
  
  boolean isAvailable()
  {
    return Build.IS_INTERNATIONAL_BUILD ^ true;
  }
  
  void translate(String paramString1, String paramString2, String paramString3)
  {
    this.mPendingTask = new TranslateTask(paramString1, paramString2, paramString3);
    if (this.mState < 1) {
      doInitialize();
    }
  }
  
  private static class TranslateTask
  {
    String mSource;
    String mTarget;
    String mWord;
    
    TranslateTask(String paramString1, String paramString2, String paramString3)
    {
      this.mSource = paramString1;
      this.mTarget = paramString2;
      this.mWord = paramString3;
    }
    
    static boolean equals(TranslateTask paramTranslateTask1, TranslateTask paramTranslateTask2)
    {
      boolean bool = true;
      if ((paramTranslateTask1 != null) && (paramTranslateTask2 != null))
      {
        if ((!TextUtils.equals(paramTranslateTask1.mSource, paramTranslateTask2.mSource)) || (!TextUtils.equals(paramTranslateTask1.mTarget, paramTranslateTask2.mTarget)) || (!TextUtils.equals(paramTranslateTask1.mWord, paramTranslateTask2.mWord))) {
          bool = false;
        }
        return bool;
      }
      return (paramTranslateTask1 == null) && (paramTranslateTask2 == null);
    }
  }
  
  private class TranslationConnection
    implements ServiceConnection
  {
    private TranslationConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      Log.d("TranslationManager", "service connected");
      TranslationManager.this.onInitialized(ITranslation.Stub.asInterface(paramIBinder));
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      Log.d("TranslationManager", "service disconnected");
      TranslationManager.this.onDisconnected();
    }
  }
  
  private class TranslationRemoteCallback
    extends ITranslationRemoteCallback.Stub
  {
    TranslationManager.TranslateTask mTask;
    
    public TranslationRemoteCallback(TranslationManager.TranslateTask paramTranslateTask)
    {
      this.mTask = paramTranslateTask;
    }
    
    public void onTranslationFinished(final TranslationResult paramTranslationResult)
    {
      Log.i("TranslationManager", "translate finish");
      TranslationManager.this.mMainHandler.post(new Runnable()
      {
        public void run()
        {
          TranslationManager.this.onTranslateDone(TranslationManager.TranslationRemoteCallback.this.mTask, paramTranslationResult);
        }
      });
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TranslationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */