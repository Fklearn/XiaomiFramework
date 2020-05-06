package services.java.com.android.server.notification;

import android.content.Context;
import android.os.SystemProperties;
import android.os.VibrationEffect;
import android.text.TextUtils;
import com.android.server.notification.NotificationRecord;

public class NotificationVibratorController {
    private static final String EFFECT_KEY_POPUP_LIGHT = "sys.haptic.popup.light";
    private static final String LINEAR_MOTOR = "linear";
    private static final String SYS_HAPTIC_MOTOR = "sys.haptic.motor";
    private static final String motorName = SystemProperties.get(SYS_HAPTIC_MOTOR, (String) null);
    private static final long[] popup_light = stringToLongArray(SystemProperties.get(EFFECT_KEY_POPUP_LIGHT, (String) null));

    public static VibrationEffect customizeNotificationVibrator(Context mContext, NotificationRecord record, VibrationEffect effect) {
        VibrationEffect vibrationEffect;
        if (isSupportLinearMotorVibrate()) {
            VibrationEffect sound_vibrationEffect = VibrationEffect.get(record.getSound(), mContext);
            if (sound_vibrationEffect != null) {
                return sound_vibrationEffect;
            }
            long[] jArr = popup_light;
            if (jArr != null && jArr.length > 0 && (vibrationEffect = VibrationEffect.get((int) jArr[0])) != null && (vibrationEffect instanceof VibrationEffect.Prebaked)) {
                ((VibrationEffect.Prebaked) vibrationEffect).setEffectStrength(2);
                return vibrationEffect;
            }
        }
        return effect;
    }

    private static boolean isSupportLinearMotorVibrate() {
        return LINEAR_MOTOR.equals(motorName);
    }

    private static long[] stringToLongArray(String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            return null;
        }
        String[] splitStr = pattern.split(",");
        int los = splitStr.length;
        long[] returnByte = new long[los];
        for (int i = 0; i < los; i++) {
            returnByte[i] = Long.parseLong(splitStr[i].trim());
        }
        return returnByte;
    }
}
