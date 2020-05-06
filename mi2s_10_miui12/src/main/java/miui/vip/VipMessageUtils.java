package miui.vip;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import miui.telephony.phonenumber.Prefix;

public class VipMessageUtils {
    static final String KEY_VALUE = "value";

    private VipMessageUtils() {
    }

    public static <T> T getValueFrom(Message msg) {
        Bundle bundle = msg.getData();
        if (bundle != null) {
            return bundle.get("value");
        }
        return null;
    }

    public static void setData(Message msg, Object value) {
        if (value != null) {
            Bundle bundle = msg.getData();
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (value instanceof String) {
                bundle.putString("value", (String) value);
            } else if (value instanceof Parcelable) {
                bundle.putParcelable("value", (Parcelable) value);
            } else if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                if (array.length > 0) {
                    if (array[0] instanceof String) {
                        bundle.putStringArray("value", (String[]) value);
                    } else if (array[0] instanceof Parcelable) {
                        bundle.putParcelableArray("value", (Parcelable[]) value);
                    }
                }
            } else if (value instanceof ArrayList) {
                ArrayList list = (ArrayList) value;
                if (!list.isEmpty()) {
                    Object elem = list.get(0);
                    if (elem instanceof String) {
                        bundle.putStringArrayList("value", (ArrayList) value);
                    } else if (elem instanceof Parcelable) {
                        bundle.putParcelableArrayList("value", (ArrayList) value);
                    }
                }
            }
            msg.setData(bundle);
        }
    }

    public static String getErrMsg(int result, Object data) {
        if (result == 0 || !(data instanceof String[])) {
            return null;
        }
        String[] res = (String[]) data;
        if (res.length >= 2) {
            return res[1];
        }
        return null;
    }

    public static String[] makeErrData(int result, Object value, String errMsg) {
        if (result == 0) {
            return null;
        }
        return new String[]{String.valueOf(value), errMsg};
    }

    public static String getRequestId(Message msg) {
        Bundle data = msg.getData();
        if (data != null) {
            return data.getString("VIP_REQUEST_ID", Prefix.EMPTY);
        }
        return Prefix.EMPTY;
    }

    public static void setRequestId(Message msg, String reqId) {
        if (!TextUtils.isEmpty(reqId)) {
            Bundle data = msg.getData();
            if (data == null) {
                data = new Bundle();
                msg.setData(data);
            }
            data.putString("VIP_REQUEST_ID", reqId);
        }
    }
}
