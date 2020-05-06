package com.miui.networkassistant.dual;

import b.b.o.g.c;
import com.miui.networkassistant.utils.TelephonyUtil;

public class Sim {
    public static final String ICCID = "iccId";
    public static final int MAX_SLOT_COUNT;
    public static final String SIM_ID = "simId";
    public static final String SIM_NAME = "simName";
    public static final String SIM_SLOT_NUM_TAG = "sim_slot_num_tag";
    public static final int SIM_STATE_READY = 5;
    public static final int SLOT1 = 0;
    public static final int SLOT2 = 1;
    public static final int SLOT_NOT_INSERTED = -1;
    public static final String SLOT_NUM = "slotNum";
    public static final String SYS_SLOT_NUM_INTENT_KEY = "slot_id";
    private static int sCurrentOptSlotNum = 0;

    static {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getPhoneCount", (Class<?>[]) null, new Object[0]);
        MAX_SLOT_COUNT = a2.c();
    }

    public static int getCurrentActiveSlotNum() {
        return TelephonyUtil.getCurrentMobileSlotNum();
    }

    public static int getCurrentOptSlotNum() {
        return sCurrentOptSlotNum;
    }

    public static void operateOnSlot1() {
        operateOnSlotNum(0);
    }

    public static void operateOnSlot2() {
        operateOnSlotNum(1);
    }

    public static void operateOnSlotNum(int i) {
        sCurrentOptSlotNum = i;
    }
}
