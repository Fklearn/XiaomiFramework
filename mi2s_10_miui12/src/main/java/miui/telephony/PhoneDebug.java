package miui.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;
import miui.content.res.ThemeResources;
import miui.os.SystemProperties;
import miui.reflect.Method;
import miui.util.AppConstants;

public class PhoneDebug {
    public static final String PHONE_DEBUG_FLAG = "phone_debug_flag";
    public static boolean VDBG;
    /* access modifiers changed from: private */
    public static List<Listener> sListeners = null;

    public interface Listener {
        void onDebugChanged();
    }

    static {
        boolean z = false;
        VDBG = false;
        try {
            Context context = AppConstants.getCurrentApplication();
            if (ThemeResources.FRAMEWORK_PACKAGE.equals(getOpPackageName(context))) {
                registerDelay(60000);
                if (Settings.System.getInt(context.getContentResolver(), PHONE_DEBUG_FLAG, 0) == 1 || SystemProperties.getBoolean("debug.miui.phone", false)) {
                    z = true;
                }
                VDBG = z;
                return;
            }
            register();
        } catch (Exception e) {
            Rlog.w("PhoneDebug", "init" + e);
        }
    }

    private PhoneDebug() {
    }

    public static Listener addListener(Listener listener) {
        if (sListeners == null) {
            sListeners = new ArrayList(1);
        }
        if (listener != null && !sListeners.contains(listener)) {
            sListeners.add(listener);
            listener.onDebugChanged();
        }
        return listener;
    }

    public static void removeListener(Listener listener) {
        List<Listener> list = sListeners;
        if (list != null && listener != null) {
            list.remove(listener);
            if (sListeners.isEmpty()) {
                sListeners = null;
            }
        }
    }

    private static void registerDelay(final int time) {
        if (VDBG) {
            Rlog.w("PhoneDebug", "registerDelay");
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep((long) time);
                    PhoneDebug.register();
                } catch (Exception te) {
                    Rlog.w("PhoneDebug", "registerDelay" + te);
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public static void register() {
        try {
            final ContentResolver cr = AppConstants.getCurrentApplication().getContentResolver();
            boolean z = true;
            if (Settings.System.getInt(cr, PHONE_DEBUG_FLAG, 0) != 1) {
                if (!SystemProperties.getBoolean("debug.miui.phone", false)) {
                    z = false;
                }
            }
            VDBG = z;
            cr.registerContentObserver(Settings.System.getUriFor(PHONE_DEBUG_FLAG), false, new ContentObserver((Handler) null) {
                public void onChange(boolean selfChange) {
                    boolean z = false;
                    if (Settings.System.getInt(cr, PhoneDebug.PHONE_DEBUG_FLAG, 0) == 1 || SystemProperties.getBoolean("debug.miui.phone", false)) {
                        z = true;
                    }
                    PhoneDebug.VDBG = z;
                    if (PhoneDebug.VDBG) {
                        Rlog.w("PhoneDebug", "onChange VDBG=" + PhoneDebug.VDBG);
                    }
                    if (PhoneDebug.sListeners != null) {
                        for (Listener l : PhoneDebug.sListeners) {
                            l.onDebugChanged();
                        }
                    }
                }
            });
        } catch (Exception te) {
            Rlog.w("PhoneDebug", "register" + te);
        }
    }

    private static String getOpPackageName(Context context) {
        return (String) Method.of(Context.class, "getOpPackageName", String.class, new Class[0]).invokeObject((Class) null, context, new Object[0]);
    }
}
