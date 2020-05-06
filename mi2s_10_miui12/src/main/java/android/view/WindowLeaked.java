package android.view;

import android.annotation.UnsupportedAppUsage;
import android.util.AndroidRuntimeException;

final class WindowLeaked
  extends AndroidRuntimeException
{
  @UnsupportedAppUsage
  public WindowLeaked(String paramString)
  {
    super(paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowLeaked.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */