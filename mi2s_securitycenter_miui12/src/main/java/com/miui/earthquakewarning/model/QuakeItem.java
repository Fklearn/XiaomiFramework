package com.miui.earthquakewarning.model;

import java.io.Serializable;
import java.util.List;

public class QuakeItem implements Serializable {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_TEST = 1;
    private String channel;
    private float depth;
    private LocationModel epiLocation = new LocationModel();
    private long eventID;
    private int index;
    private boolean isValidate;
    private float magnitude;
    private List<String> signature;
    private String signatureText;
    private long startTime = 0;
    private int type;
    private long updateTime = 0;

    public String getChannel() {
        return this.channel;
    }

    public float getDepth() {
        return this.depth;
    }

    public LocationModel getEpiLocation() {
        return this.epiLocation;
    }

    public long getEventID() {
        return this.eventID;
    }

    public int getIndex() {
        return this.index;
    }

    public float getMagnitude() {
        return this.magnitude;
    }

    public List<String> getSignature() {
        return this.signature;
    }

    public String getSignatureText() {
        return this.signatureText;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getType() {
        return this.type;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public boolean isValidate() {
        return this.isValidate;
    }

    public void setChannel(String str) {
        this.channel = str;
    }

    public void setDepth(float f) {
        this.depth = f;
    }

    public void setEpiLocation(LocationModel locationModel) {
        this.epiLocation = locationModel;
    }

    public void setEventID(long j) {
        this.eventID = j;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public void setMagnitude(float f) {
        this.magnitude = f;
    }

    public void setSignature(List<String> list) {
        this.signature = list;
    }

    public void setSignatureText(String str) {
        this.signatureText = str;
    }

    public void setStartTime(long j) {
        this.startTime = j;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setUpdateTime(long j) {
        this.updateTime = j;
    }

    public void setValidate(boolean z) {
        this.isValidate = z;
    }
}
