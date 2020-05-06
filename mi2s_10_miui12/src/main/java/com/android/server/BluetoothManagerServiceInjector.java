package com.android.server;

class BluetoothManagerServiceInjector {
    static final int BD_ADDR_NO_COLON_LEN = 12;
    static final int BD_ADDR_WITH_COLON_LEN = 17;
    private static final boolean DBG = true;
    static final String MASK_BD_ADDR_NO_COLON_PREFIX = "000000";
    static final String MASK_BD_ADDR_WITH_COLON_DEFAULT = "00:00:00:00:00:00";
    static final String MASK_BD_ADDR_WITH_COLON_PREFIX = "00:00:00:";
    private static final String TAG = "BluetoothManagerServiceInjector";

    BluetoothManagerServiceInjector() {
    }

    public static String getMaskDeviceAddress(String address) {
        if (address == null) {
            return MASK_BD_ADDR_WITH_COLON_DEFAULT;
        }
        if (address.length() >= 17) {
            return MASK_BD_ADDR_WITH_COLON_PREFIX + address.substring(MASK_BD_ADDR_WITH_COLON_PREFIX.length());
        } else if (address.length() < 12) {
            return MASK_BD_ADDR_WITH_COLON_DEFAULT;
        } else {
            return MASK_BD_ADDR_NO_COLON_PREFIX + address.substring(MASK_BD_ADDR_NO_COLON_PREFIX.length());
        }
    }
}
