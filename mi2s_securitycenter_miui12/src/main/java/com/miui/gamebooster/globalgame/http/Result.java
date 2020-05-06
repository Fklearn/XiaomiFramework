package com.miui.gamebooster.globalgame.http;

import android.support.annotation.Keep;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.miui.gamebooster.globalgame.util.NoProguard;

@Keep
@NoProguard
public class Result {
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("head")
    @Expose
    private Head head;

    public String getData() {
        return this.data;
    }

    public Head getHead() {
        return this.head;
    }

    public void setData(String str) {
        this.data = str;
    }

    public void setHead(Head head2) {
        this.head = head2;
    }
}
