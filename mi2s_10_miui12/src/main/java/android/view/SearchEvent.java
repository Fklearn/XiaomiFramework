package android.view;

public class SearchEvent
{
  private InputDevice mInputDevice;
  
  public SearchEvent(InputDevice paramInputDevice)
  {
    this.mInputDevice = paramInputDevice;
  }
  
  public InputDevice getInputDevice()
  {
    return this.mInputDevice;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SearchEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */