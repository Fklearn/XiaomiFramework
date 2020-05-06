package com.miui.gamebooster.model;

import android.support.annotation.Keep;
import com.miui.gamebooster.globalgame.util.NoProguard;
import java.text.SimpleDateFormat;
import java.util.Date;

@Keep
@NoProguard
public class ActiveTrackModel {

    /* renamed from: a  reason: collision with root package name */
    private String f4534a;
    private String date;
    private String game;
    private String i;
    private String id;
    private transient SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String s;
    private int times;
    private String type;

    public ActiveTrackModel(String str, String str2, String str3, String str4, String str5, String str6) {
        this.id = str;
        this.type = str2;
        this.s = str3;
        this.i = str4;
        this.f4534a = str5;
        this.game = str6;
        this.date = this.mFormat.format(new Date(System.currentTimeMillis()));
    }

    public String getA() {
        return this.f4534a;
    }

    public String getDate() {
        return this.date;
    }

    public String getGame() {
        return this.game;
    }

    public String getI() {
        return this.i;
    }

    public String getId() {
        return this.id;
    }

    public String getS() {
        return this.s;
    }

    public int getTimes() {
        return this.times;
    }

    public String getType() {
        return this.type;
    }

    public void setA(String str) {
        this.f4534a = str;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public void setGame(String str) {
        this.game = str;
    }

    public void setI(String str) {
        this.i = str;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setS(String str) {
        this.s = str;
    }

    public void setTimes(int i2) {
        this.times = i2;
    }

    public void setType(String str) {
        this.type = str;
    }
}
