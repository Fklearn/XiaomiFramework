package android.view.inputmethod;

import android.os.IBinder;
import android.os.ResultReceiver;
import com.android.internal.inputmethod.IInputMethodPrivilegedOperations;

public abstract interface InputMethod
{
  public static final String SERVICE_INTERFACE = "android.view.InputMethod";
  public static final String SERVICE_META_DATA = "android.view.im";
  public static final int SHOW_EXPLICIT = 1;
  public static final int SHOW_FORCED = 2;
  
  public abstract void attachToken(IBinder paramIBinder);
  
  public abstract void bindInput(InputBinding paramInputBinding);
  
  public abstract void changeInputMethodSubtype(InputMethodSubtype paramInputMethodSubtype);
  
  public abstract void createSession(SessionCallback paramSessionCallback);
  
  public void dispatchStartInputWithToken(InputConnection paramInputConnection, EditorInfo paramEditorInfo, boolean paramBoolean1, IBinder paramIBinder, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      restartInput(paramInputConnection, paramEditorInfo);
    } else {
      startInput(paramInputConnection, paramEditorInfo);
    }
  }
  
  public abstract void hideSoftInput(int paramInt, ResultReceiver paramResultReceiver);
  
  public void initializeInternal(IBinder paramIBinder, int paramInt, IInputMethodPrivilegedOperations paramIInputMethodPrivilegedOperations)
  {
    updateInputMethodDisplay(paramInt);
    attachToken(paramIBinder);
  }
  
  public abstract void restartInput(InputConnection paramInputConnection, EditorInfo paramEditorInfo);
  
  public abstract void revokeSession(InputMethodSession paramInputMethodSession);
  
  public abstract void setSessionEnabled(InputMethodSession paramInputMethodSession, boolean paramBoolean);
  
  public abstract void showSoftInput(int paramInt, ResultReceiver paramResultReceiver);
  
  public abstract void startInput(InputConnection paramInputConnection, EditorInfo paramEditorInfo);
  
  public abstract void unbindInput();
  
  public void updateInputMethodDisplay(int paramInt) {}
  
  public static abstract interface SessionCallback
  {
    public abstract void sessionCreated(InputMethodSession paramInputMethodSession);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */