package com.miui.sdk.tc;

import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public class TcDirection {
    private static final String KEY_CMD_TYPE = "cmdType";
    private static final String KEY_CONTROL_NUMBER = "controlNumber";
    private static final String KEY_DIRECTION = "direction";
    private static final String KEY_RECEIVE_NUMBER = "receiveNumber";
    private static final String KEY_SEND_NUMBER = "sendNumber";
    private int mCmdType;
    private String mControlNumber;
    private String mDirection;
    private String mReceiveNumber;
    private String mSendNumber;

    public TcDirection(String str) {
        JSONObject jSONObject = new JSONObject(str);
        this.mSendNumber = jSONObject.getString(KEY_SEND_NUMBER);
        this.mDirection = jSONObject.getString(KEY_DIRECTION);
        this.mReceiveNumber = jSONObject.optString(KEY_RECEIVE_NUMBER, (String) null);
        this.mControlNumber = jSONObject.optString(KEY_CONTROL_NUMBER, (String) null);
        this.mCmdType = jSONObject.getInt(KEY_CMD_TYPE);
    }

    public TcDirection(String str, String str2, int i) {
        this.mSendNumber = str;
        this.mDirection = str2;
        this.mReceiveNumber = null;
        this.mControlNumber = null;
        this.mCmdType = i;
    }

    public TcDirection(String str, String str2, String str3, String str4) {
        this.mSendNumber = str;
        this.mDirection = str2;
        this.mReceiveNumber = str3;
        this.mControlNumber = str4;
    }

    public TcDirection(String str, String str2, String str3, String str4, int i) {
        this.mSendNumber = str;
        this.mDirection = str2;
        this.mReceiveNumber = str3;
        this.mControlNumber = str4;
        this.mCmdType = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || TcDirection.class != obj.getClass()) {
            return false;
        }
        TcDirection tcDirection = (TcDirection) obj;
        return this.mCmdType == tcDirection.mCmdType && Objects.equals(this.mSendNumber, tcDirection.mSendNumber) && Objects.equals(this.mDirection, tcDirection.mDirection) && Objects.equals(this.mReceiveNumber, tcDirection.mReceiveNumber) && Objects.equals(this.mControlNumber, tcDirection.mControlNumber);
    }

    public int getCmdType() {
        return this.mCmdType;
    }

    public String getControlNumber() {
        return this.mControlNumber;
    }

    public String getDirection() {
        return this.mDirection;
    }

    public String getReceiveNumber() {
        return this.mReceiveNumber;
    }

    public String getSendNumber() {
        return this.mSendNumber;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mSendNumber, this.mDirection, this.mReceiveNumber, this.mControlNumber, Integer.valueOf(this.mCmdType)});
    }

    public void setCmdType(int i) {
        this.mCmdType = i;
    }

    public void setControlNumber(String str) {
        this.mControlNumber = str;
    }

    public void setDirection(String str) {
        this.mDirection = str;
    }

    public void setReceiveNumber(String str) {
        this.mReceiveNumber = str;
    }

    public void setSendNumber(String str) {
        this.mSendNumber = str;
    }

    public JSONObject toJSON() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_SEND_NUMBER, this.mSendNumber);
            jSONObject.put(KEY_DIRECTION, this.mDirection);
            jSONObject.put(KEY_RECEIVE_NUMBER, this.mReceiveNumber);
            jSONObject.put(KEY_CONTROL_NUMBER, this.mControlNumber);
            jSONObject.put(KEY_CMD_TYPE, this.mCmdType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public String toString() {
        return String.format("mSendNumber:%s mDirection:%s mReceiveNumber:%s mControlNumber:%s mCmdType:%s", new Object[]{this.mSendNumber, this.mDirection, this.mReceiveNumber, this.mControlNumber, Integer.valueOf(this.mCmdType)});
    }
}
