package miui.util;

import android.content.Context;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;

public class LockPatternUtilsWrapper
{
  public static int getActivePasswordQuality(Context paramContext)
  {
    return new LockPatternUtils(paramContext).getActivePasswordQuality(UserHandle.myUserId());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/LockPatternUtilsWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */