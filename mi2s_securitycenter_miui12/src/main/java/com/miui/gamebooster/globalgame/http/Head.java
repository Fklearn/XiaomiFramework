package com.miui.gamebooster.globalgame.http;

import android.support.annotation.Keep;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.miui.gamebooster.globalgame.util.NoProguard;

@Keep
@NoProguard
public class Head {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("version")
    @Expose
    private String version;

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getServer() {
        return this.server;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Long getTime() {
        return this.time;
    }

    public String getVersion() {
        return this.version;
    }

    public void setCode(Integer num) {
        this.code = num;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public void setServer(String str) {
        this.server = str;
    }

    public void setStatus(Integer num) {
        this.status = num;
    }

    public void setTime(Long l) {
        this.time = l;
    }

    public void setVersion(String str) {
        this.version = str;
    }
}
