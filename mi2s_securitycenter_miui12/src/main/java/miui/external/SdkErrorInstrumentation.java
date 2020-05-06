package miui.external;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import java.lang.reflect.Field;
import miui.external.SdkConstants;

final class SdkErrorInstrumentation extends Instrumentation implements SdkConstants {
    private SdkConstants.SdkError mError;

    private SdkErrorInstrumentation(SdkConstants.SdkError sdkError) {
        this.mError = sdkError;
    }

    private static Field getDeclaredField(Class<?> cls, Object obj, Object obj2, String str, Class<?> cls2) {
        Field[] declaredFields = cls.getDeclaredFields();
        if (!(obj == null || obj2 == null)) {
            int length = declaredFields.length;
            int i = 0;
            while (i < length) {
                Field field = declaredFields[i];
                field.setAccessible(true);
                try {
                    if (field.get(obj) == obj2) {
                        return field;
                    }
                    i++;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (str != null) {
            for (Field field2 : declaredFields) {
                if (field2.getName().equals(str)) {
                    field2.setAccessible(true);
                    return field2;
                }
            }
        }
        Field field3 = null;
        if (cls2 == null) {
            for (Field field4 : declaredFields) {
                if (field4.getType() == cls2 || field4.getType().isInstance(cls2)) {
                    if (field3 == null) {
                        field3 = field4;
                    } else {
                        throw new NoSuchFieldException("More than one matched field found: " + field3.getName() + " and " + field4.getName());
                    }
                }
            }
            if (field3 != null) {
                field3.setAccessible(true);
            } else {
                throw new NoSuchFieldException("No such field found of value " + obj2);
            }
        }
        return field3;
    }

    static void handleSdkError(SdkConstants.SdkError sdkError) {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Object invoke = cls.getMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]);
            Field declaredField = getDeclaredField(cls, invoke, (Instrumentation) cls.getMethod("getInstrumentation", new Class[0]).invoke(invoke, new Object[0]), (String) null, (Class<?>) null);
            Instrumentation instrumentation = (Instrumentation) declaredField.get(invoke);
            SdkErrorInstrumentation sdkErrorInstrumentation = new SdkErrorInstrumentation(sdkError);
            for (Class cls2 = Instrumentation.class; cls2 != null; cls2 = cls2.getSuperclass()) {
                for (Field field : cls2.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(sdkErrorInstrumentation, field.get(instrumentation));
                }
            }
            declaredField.set(invoke, sdkErrorInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) {
        Intent intent2;
        Class<SdkErrorActivity> cls2;
        Intent intent3;
        SdkErrorInstrumentation sdkErrorInstrumentation;
        if (!cls.getSimpleName().startsWith("SdkError")) {
            Class<SdkErrorActivity> cls3 = SdkErrorActivity.class;
            if (intent == null) {
                intent3 = new Intent();
                sdkErrorInstrumentation = this;
            } else {
                sdkErrorInstrumentation = this;
                intent3 = intent;
            }
            intent3.putExtra(SdkConstants.SdkError.INTENT_EXTRA_KEY, sdkErrorInstrumentation.mError);
            cls2 = cls3;
            intent2 = intent3;
        } else {
            cls2 = cls;
            intent2 = intent;
        }
        return super.newActivity(cls2, context, iBinder, application, intent2, activityInfo, charSequence, activity, str, obj);
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) {
        if (!str.startsWith("SdkError")) {
            str = SdkErrorActivity.class.getName();
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(SdkConstants.SdkError.INTENT_EXTRA_KEY, this.mError);
        }
        return super.newActivity(classLoader, str, intent);
    }
}
