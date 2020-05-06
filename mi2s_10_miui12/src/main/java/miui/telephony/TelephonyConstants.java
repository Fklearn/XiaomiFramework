package miui.telephony;

public class TelephonyConstants {
    public static final String ACTION_CDMA_CALL_REAL_CONNECTED = "miui.intent.action.ACTION_CDMA_CALL_REAL_CONNECTED";
    public static final String ACTION_DEVICE_ID_READY = "android.intent.action.DEVICE_ID_READY";
    public static final String ACTION_ENHANCED_4G_LTE_MODE_CHANGE_FOR_SLOT1 = "miui.intent.action.ACTION_ENHANCED_4G_LTE_MODE_CHANGE_FOR_SLOT1";
    public static final String ACTION_ENHANCED_4G_LTE_MODE_CHANGE_FOR_SLOT2 = "miui.intent.action.ACTION_ENHANCED_4G_LTE_MODE_CHANGE_FOR_SLOT2";
    public static final String ACTION_IMS_REGISTED = "android.intent.action.ACTION_IMS_REGISTED";
    public static final String ACTION_SPEECH_CODEC_IS_HD = "android.intent.action.ACTION_SPEECH_CODEC_IS_HD";
    public static final String EXTRA_ADD_PARTICIPANT_KEY = "add_participant";
    public static final String EXTRA_DEVICE_ID = "device_id";
    public static final String EXTRA_DIAL_CONFERENCE_URI = "org.codeaurora.extra.DIAL_CONFERENCE_URI";
    public static final String EXTRA_IMS_REGISTED_STATE = "state";
    public static final String EXTRA_IS_ENHANCED_4G_LTE_ON = "extra_is_enhanced_4g_lte_on";
    public static final String EXTRA_IS_HD = "is_hd";
    public static final String EXTRA_START_CALL_WITH_VIDEO_STATE = "android.telecom.extra.START_CALL_WITH_VIDEO_STATE";
    public static final String EXTRA_WFC_REGISTED_STATE = "wfc_state";
    public static String PROPERTY_APN_SIM_OPERATOR_NUMERIC = "gsm.apn.sim.operator.numeric";
    public static final String PROPERTY_DEVICE_ID = "ro.ril.miui.imei";
    public static final int STATE_AUDIO_ONLY = 0;
    public static final int STATE_BIDIRECTIONAL = 3;
    public static final int STATE_RX_ENABLED = 2;
    public static final int STATE_TX_ENABLED = 1;
}
