package miui.cloud.backup.data;

import android.content.SharedPreferences;
import android.util.Log;
import java.util.Map;

public class PrefsBackupHelper {
    private static final String TAG = "PrefsBackupHelper";

    private PrefsBackupHelper() {
    }

    public static class PrefEntry {
        private String mCloudKey;
        private Object mDefaultValue;
        private String mLocalKey;
        private Class<?> mValueClass;

        private PrefEntry(String cloudKey, String localKey, Class<?> valueClass, Object defaultValue) {
            this.mCloudKey = cloudKey;
            this.mLocalKey = localKey;
            this.mValueClass = valueClass;
            this.mDefaultValue = defaultValue;
        }

        public String getCloudKey() {
            return this.mCloudKey;
        }

        public String getLocalKey() {
            return this.mLocalKey;
        }

        public Class<?> getValueClass() {
            return this.mValueClass;
        }

        public Object getDefaultValue() {
            return this.mDefaultValue;
        }

        public static PrefEntry createIntEntry(String cloudKey, String localKey, int defaultValue) {
            return new PrefEntry(cloudKey, localKey, Integer.class, Integer.valueOf(defaultValue));
        }

        public static PrefEntry createIntEntry(String cloudKey, String localKey) {
            return new PrefEntry(cloudKey, localKey, Integer.class, (Object) null);
        }

        public static PrefEntry createLongEntry(String cloudKey, String localKey, long defaultValue) {
            return new PrefEntry(cloudKey, localKey, Long.class, Long.valueOf(defaultValue));
        }

        public static PrefEntry createLongEntry(String cloudKey, String localKey) {
            return new PrefEntry(cloudKey, localKey, Long.class, (Object) null);
        }

        public static PrefEntry createBoolEntry(String cloudKey, String localKey, boolean defaultValue) {
            return new PrefEntry(cloudKey, localKey, Boolean.class, Boolean.valueOf(defaultValue));
        }

        public static PrefEntry createBoolEntry(String cloudKey, String localKey) {
            return new PrefEntry(cloudKey, localKey, Boolean.class, (Object) null);
        }

        public static PrefEntry createStringEntry(String cloudKey, String localKey, String defaultValue) {
            return new PrefEntry(cloudKey, localKey, String.class, defaultValue);
        }

        public static PrefEntry createStringEntry(String cloudKey, String localKey) {
            return new PrefEntry(cloudKey, localKey, String.class, (Object) null);
        }
    }

    public static void backup(SharedPreferences prefs, DataPackage dataPackage, PrefEntry[] entries) {
        Map<String, ?> map = prefs.getAll();
        for (PrefEntry entry : entries) {
            Object value = map.get(entry.getLocalKey());
            if (value != null) {
                if (value.getClass() == entry.getValueClass()) {
                    dataPackage.addKeyValue(entry.getCloudKey(), value.toString());
                } else {
                    throw new IllegalStateException("Preference type of " + entry.getLocalKey() + " mismatched. actual type = " + value.getClass().getSimpleName() + ", expected type = " + entry.getValueClass().getSimpleName());
                }
            } else if (entry.getDefaultValue() != null) {
                dataPackage.addKeyValue(entry.getCloudKey(), entry.getDefaultValue().toString());
            }
        }
    }

    public static void restore(SharedPreferences prefs, DataPackage dataPackage, PrefEntry[] entries) {
        SharedPreferences.Editor editor = prefs.edit();
        for (PrefEntry entry : entries) {
            try {
                KeyStringSettingItem item = (KeyStringSettingItem) dataPackage.get(entry.getCloudKey());
                if (item != null) {
                    String value = (String) item.getValue();
                    if (entry.getValueClass() == Integer.class) {
                        editor.putInt(entry.getLocalKey(), Integer.parseInt(value));
                    } else if (entry.getValueClass() == Long.class) {
                        editor.putLong(entry.getLocalKey(), Long.parseLong(value));
                    } else if (entry.getValueClass() == Boolean.class) {
                        editor.putBoolean(entry.getLocalKey(), Boolean.parseBoolean(value));
                    } else if (entry.getValueClass() == String.class) {
                        editor.putString(entry.getLocalKey(), value);
                    }
                } else {
                    editor.remove(entry.getLocalKey());
                }
            } catch (ClassCastException e) {
                Log.e(TAG, "entry " + entry.getCloudKey() + " is not KeyStringSettingItem");
            }
        }
        editor.commit();
    }
}
