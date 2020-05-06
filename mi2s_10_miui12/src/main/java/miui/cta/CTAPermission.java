package miui.cta;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import com.miui.system.internal.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CTAPermission {
    private static final String TAG = "CTAPermission";

    private enum Permission {
        PERMISSION_ACCESS_NETWORK(1),
        PERMISSION_READ_SMS(2),
        PERMISSION_WRITE_SMS(4),
        PERMISSION_RECEIVE_SMS(8),
        PERMISSION_SEND_SMS(16),
        PERMISSION_CALL_PHONE(32),
        PERMISSION_READ_CONTACTS(64),
        PERMISSION_WRITE_CONTACTS(128),
        PERMISSION_READ_CALL_LOG(256),
        PERMISSION_WRITE_CALL_LOG(512),
        PERMISSION_CAMERA(1024),
        PERMISSION_ACCESS_LOCATION(2048);
        
        final int value;

        private Permission(int value2) {
            this.value = value2;
        }

        public String toString() {
            return super.toString() + ", value=" + this.value;
        }
    }

    public static String getMessage(Context context, int permValue) {
        if (permValue == 0) {
            return null;
        }
        List<String> permNames = getPermissionNames(context, permValue);
        if (permNames.isEmpty()) {
            return null;
        }
        return getPermissionMessage(context, permNames);
    }

    private static List<String> getPermissionNames(Context context, int permValue) {
        ArrayList<String> permNames = new ArrayList<>();
        SparseArray<String> permMap = getPermissionMap(context);
        for (int index = permMap.size() - 1; index >= 0; index--) {
            int key = permMap.keyAt(index);
            if ((permValue & key) == key) {
                permNames.add(permMap.valueAt(index));
                permValue &= ~key;
            }
        }
        Collections.reverse(permNames);
        return permNames;
    }

    private static SparseArray<String> getPermissionMap(Context context) {
        Resources resources = context.getResources();
        String[] permissions = resources.getStringArray(R.array.cta_permissions);
        String[] permissionNames = resources.getStringArray(R.array.cta_permission_names);
        SparseArray<String> permMap = new SparseArray<>();
        int N = Math.min(permissions.length, permissionNames.length);
        for (int i = 0; i < N; i++) {
            int value = 0;
            for (String perm : permissions[i].split("\\|")) {
                try {
                    value |= Permission.valueOf(perm).value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Unknown permission: " + perm, e);
                }
            }
            if (value > 0) {
                permMap.append(value, permissionNames[i]);
            }
        }
        return permMap;
    }

    private static String getPermissionMessage(Context context, List<String> permNames) {
        int N = permNames.size();
        if (N == 1) {
            return permNames.get(0);
        }
        if (N == 2) {
            String and = context.getString(R.string.cta_permission_and);
            return permNames.get(0) + and + permNames.get(1);
        }
        String delimiter = context.getString(R.string.cta_permission_delimiter);
        String and2 = context.getString(R.string.cta_permission_and);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < N - 2; i++) {
            out.append(permNames.get(i));
            out.append(delimiter);
        }
        out.append(permNames.get(N - 2));
        out.append(and2);
        out.append(permNames.get(N - 1));
        return out.toString();
    }
}
