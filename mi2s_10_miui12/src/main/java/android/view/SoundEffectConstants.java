package android.view;

public class SoundEffectConstants
{
  public static final int CLICK = 0;
  public static final int NAVIGATION_DOWN = 4;
  public static final int NAVIGATION_LEFT = 1;
  public static final int NAVIGATION_RIGHT = 3;
  public static final int NAVIGATION_UP = 2;
  
  public static int getContantForFocusDirection(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2) {
        if (paramInt != 17)
        {
          if (paramInt == 33) {
            break label54;
          }
          if (paramInt != 66)
          {
            if (paramInt != 130) {
              throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
            }
          }
          else {
            return 3;
          }
        }
        else
        {
          return 1;
        }
      }
      return 4;
    }
    label54:
    return 2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SoundEffectConstants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */