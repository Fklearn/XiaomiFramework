package android.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.util.ArrayList;
import java.util.List;

public class InputEventCompatProcessor
{
  protected Context mContext;
  private List<InputEvent> mProcessedEvents;
  protected int mTargetSdkVersion;
  
  public InputEventCompatProcessor(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTargetSdkVersion = paramContext.getApplicationInfo().targetSdkVersion;
    this.mProcessedEvents = new ArrayList();
  }
  
  public InputEvent processInputEventBeforeFinish(InputEvent paramInputEvent)
  {
    return paramInputEvent;
  }
  
  public List<InputEvent> processInputEventForCompatibility(InputEvent paramInputEvent)
  {
    if ((this.mTargetSdkVersion < 23) && ((paramInputEvent instanceof MotionEvent)))
    {
      this.mProcessedEvents.clear();
      paramInputEvent = (MotionEvent)paramInputEvent;
      int i = paramInputEvent.getButtonState();
      int j = (i & 0x60) >> 4;
      if (j != 0) {
        paramInputEvent.setButtonState(i | j);
      }
      this.mProcessedEvents.add(paramInputEvent);
      return this.mProcessedEvents;
    }
    return null;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputEventCompatProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */