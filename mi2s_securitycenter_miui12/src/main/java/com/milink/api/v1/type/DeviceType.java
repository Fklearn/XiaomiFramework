package com.milink.api.v1.type;

public enum DeviceType {
    Unknown,
    TV,
    Speaker;
    
    private static final String AIRKAN = "airkan";
    private static final String AIRPLAY = "airplay";
    private static final String AIRTUNES = "airtunes";
    private static final String DLNA_SPEAKER = "dlna.speaker";
    private static final String DLNA_TV = "dlna.tv";

    public static DeviceType create(String str) {
        return str.equalsIgnoreCase(AIRKAN) ? TV : str.equalsIgnoreCase(AIRPLAY) ? TV : str.equalsIgnoreCase(AIRTUNES) ? Speaker : str.equalsIgnoreCase(DLNA_TV) ? TV : str.equalsIgnoreCase(DLNA_SPEAKER) ? Speaker : Unknown;
    }
}
