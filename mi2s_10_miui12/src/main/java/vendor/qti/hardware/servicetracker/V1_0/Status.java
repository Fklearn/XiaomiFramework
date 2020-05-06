package vendor.qti.hardware.servicetracker.V1_0;

import java.util.ArrayList;

public final class Status {
    public static final int ERROR_INVALID_ARGS = 2;
    public static final int ERROR_NOT_AVAILABLE = 1;
    public static final int ERROR_NOT_SUPPORTED = 3;
    public static final int ERROR_UNKNOWN = 4;
    public static final int SUCCESS = 0;

    public static final String toString(int o) {
        if (o == 0) {
            return "SUCCESS";
        }
        if (o == 1) {
            return "ERROR_NOT_AVAILABLE";
        }
        if (o == 2) {
            return "ERROR_INVALID_ARGS";
        }
        if (o == 3) {
            return "ERROR_NOT_SUPPORTED";
        }
        if (o == 4) {
            return "ERROR_UNKNOWN";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("SUCCESS");
        if ((o & 1) == 1) {
            list.add("ERROR_NOT_AVAILABLE");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("ERROR_INVALID_ARGS");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("ERROR_NOT_SUPPORTED");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("ERROR_UNKNOWN");
            flipped |= 4;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
