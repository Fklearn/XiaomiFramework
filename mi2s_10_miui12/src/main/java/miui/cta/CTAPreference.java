package miui.cta;

import android.content.Context;
import android.preference.PreferenceManager;
import miui.util.FBEUtils;

public class CTAPreference {
    private static final String PREF_KEY_ACCEPTED = "miui.cta.pref.accepted";

    public static void setAccepted(Context context, boolean accepted) {
        PreferenceManager.getDefaultSharedPreferences(FBEUtils.getSafeStorageContext(context)).edit().putBoolean(PREF_KEY_ACCEPTED, accepted).apply();
    }

    public static boolean isAccepted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(FBEUtils.getSafeStorageContext(context)).getBoolean(PREF_KEY_ACCEPTED, false);
    }
}
