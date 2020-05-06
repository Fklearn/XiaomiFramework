package com.milink.api.v1.type;

public enum DeviceType {
    Unknown,
    TV,
    Speaker,
    Miracast,
    Lelink,
    Bluetooth;
    
    public static final String AIRKAN = "airkan";
    public static final String AIRPLAY = "airplay";
    public static final String AIRTUNES = "airtunes";
    public static final String BLUETOOTH = "bluetooth";
    public static final String DLNA_SPEAKER = "dlna.speaker";
    public static final String DLNA_TV = "dlna.tv";
    public static final String LELINK = "lelink";
    public static final String MIRACAST = "miracast";

    public static DeviceType create(String type) {
        if (type.equalsIgnoreCase(AIRKAN)) {
            return TV;
        }
        if (type.equalsIgnoreCase(AIRPLAY)) {
            return TV;
        }
        if (type.equalsIgnoreCase(AIRTUNES)) {
            return Speaker;
        }
        if (type.equalsIgnoreCase(DLNA_TV)) {
            return TV;
        }
        if (type.equalsIgnoreCase(DLNA_SPEAKER)) {
            return Speaker;
        }
        if (type.equalsIgnoreCase(MIRACAST)) {
            return Miracast;
        }
        if (type.equalsIgnoreCase(LELINK)) {
            return Lelink;
        }
        if (type.equalsIgnoreCase(BLUETOOTH)) {
            return Bluetooth;
        }
        return Unknown;
    }
}
