package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public abstract class InputFilter
  extends IInputFilter.Stub
{
  private static final int MSG_INPUT_EVENT = 3;
  private static final int MSG_INSTALL = 1;
  private static final int MSG_UNINSTALL = 2;
  private final H mH;
  private IInputFilterHost mHost;
  private final InputEventConsistencyVerifier mInboundInputEventConsistencyVerifier;
  private final InputEventConsistencyVerifier mOutboundInputEventConsistencyVerifier;
  
  @UnsupportedAppUsage
  public InputFilter(Looper paramLooper)
  {
    boolean bool = InputEventConsistencyVerifier.isInstrumentationEnabled();
    Object localObject1 = null;
    Object localObject2;
    if (bool) {
      localObject2 = new InputEventConsistencyVerifier(this, 1, "InputFilter#InboundInputEventConsistencyVerifier");
    } else {
      localObject2 = null;
    }
    this.mInboundInputEventConsistencyVerifier = ((InputEventConsistencyVerifier)localObject2);
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
      localObject2 = new InputEventConsistencyVerifier(this, 1, "InputFilter#OutboundInputEventConsistencyVerifier");
    } else {
      localObject2 = localObject1;
    }
    this.mOutboundInputEventConsistencyVerifier = ((InputEventConsistencyVerifier)localObject2);
    this.mH = new H(paramLooper);
  }
  
  public final void filterInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    this.mH.obtainMessage(3, paramInt, 0, paramInputEvent).sendToTarget();
  }
  
  public final void install(IInputFilterHost paramIInputFilterHost)
  {
    this.mH.obtainMessage(1, paramIInputFilterHost).sendToTarget();
  }
  
  @UnsupportedAppUsage
  public void onInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    sendInputEvent(paramInputEvent, paramInt);
  }
  
  public void onInstalled() {}
  
  public void onUninstalled() {}
  
  public void sendInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    if (paramInputEvent != null)
    {
      if (this.mHost != null)
      {
        InputEventConsistencyVerifier localInputEventConsistencyVerifier = this.mOutboundInputEventConsistencyVerifier;
        if (localInputEventConsistencyVerifier != null) {
          localInputEventConsistencyVerifier.onInputEvent(paramInputEvent, 0);
        }
        try
        {
          this.mHost.sendInputEvent(paramInputEvent, paramInt);
        }
        catch (RemoteException paramInputEvent) {}
        return;
      }
      throw new IllegalStateException("Cannot send input event because the input filter is not installed.");
    }
    throw new IllegalArgumentException("event must not be null");
  }
  
  public final void uninstall()
  {
    this.mH.obtainMessage(2).sendToTarget();
  }
  
  private final class H
    extends Handler
  {
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3) {
            return;
          }
          InputEvent localInputEvent = (InputEvent)paramMessage.obj;
          try
          {
            if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
              InputFilter.this.mInboundInputEventConsistencyVerifier.onInputEvent(localInputEvent, 0);
            }
            InputFilter.this.onInputEvent(localInputEvent, paramMessage.arg1);
          }
          finally
          {
            localInputEvent.recycle();
          }
        }
      }
      try
      {
        InputFilter.this.onUninstalled();
        InputFilter.access$002(InputFilter.this, null);
      }
      finally
      {
        InputFilter.access$002(InputFilter.this, null);
      }
      if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
        InputFilter.this.mInboundInputEventConsistencyVerifier.reset();
      }
      if (InputFilter.this.mOutboundInputEventConsistencyVerifier != null) {
        InputFilter.this.mOutboundInputEventConsistencyVerifier.reset();
      }
      InputFilter.this.onInstalled();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */