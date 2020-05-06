package miui.mipub;

import android.content.ContentResolver;
import android.provider.Settings;

@Deprecated
public class MipubUtils {
    private static final String TAG = "MipubUtils";

    @Deprecated
    public static boolean hasFollowedMipubs(ContentResolver cr) {
        if (Settings.System.getInt(cr, "has_followed_mipub", 0) == 1) {
            return true;
        }
        return false;
    }

    @Deprecated
    public static void setHasFollowedMipubs(ContentResolver cr, boolean followed) {
        int i;
        if (followed) {
            i = 1;
        } else {
            i = 0;
        }
        Settings.System.putInt(cr, "has_followed_mipub", i);
    }
}
