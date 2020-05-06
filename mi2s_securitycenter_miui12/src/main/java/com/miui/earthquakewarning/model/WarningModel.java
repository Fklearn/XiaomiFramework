package com.miui.earthquakewarning.model;

public class WarningModel {
    public int _id;
    public int depth;
    public double distance;
    public String epicenter;
    public int eventID;
    public int index_ew;
    public float intensity;
    public double latitude;
    public double longitude;
    public float magnitude;
    public double myLatitude;
    public double myLongitude;
    public String signature;
    public long startTime;
    public int type;
    public long updateTime;
    public int warnTime;

    public interface Columns {
        public static final String DEPTH = "depth";
        public static final String DISTANCE = "distance";
        public static final String EPICENTER = "epicenter";
        public static final String EVENTID = "eventID";
        public static final String ID = "_id";
        public static final String INDEX_EW = "index_ew";
        public static final String INTENSITY = "intensity";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String MAGNITUDE = "magnitude";
        public static final String MYLATITUDE = "myLatitude";
        public static final String MYLONGITUDE = "myLongitude";
        public static final String SIGNATURE = "signature";
        public static final String STARTTIME = "startTime";
        public static final String TYPE = "type";
        public static final String UPDATETIME = "updateTime";
        public static final String WARNTIME = "warntime";
    }
}
