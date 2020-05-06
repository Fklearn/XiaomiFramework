package miui.util;

import android.content.Context;

public class DiracFeature {
    public static boolean isSupportDirac(Context context) {
        return context.getResources().getBoolean(context.getResources().getIdentifier("is_dirac_supported", "bool", "android.miui"));
    }
}
