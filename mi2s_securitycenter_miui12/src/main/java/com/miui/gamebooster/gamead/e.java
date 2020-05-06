package com.miui.gamebooster.gamead;

import android.content.Context;
import android.view.View;
import com.miui.antivirus.result.C0238a;
import java.io.Serializable;

public abstract class e extends C0238a implements View.OnClickListener, Serializable {
    public static final String TYPE_ACTIVITY = "003";
    public static final String TYPE_ADVERTISEMENT = "001";
    public static final String TYPE_ADVERTISEMENT_TEST = "0010";
    public static final String TYPE_CRAD = "006";
    public static final String TYPE_FUNCTION = "002";
    public static final String TYPE_LINE = "005";
    public static final String TYPE_NEWS = "004";
    public static final String TYPE_VIEWPOINTS = "010";
    private static final long serialVersionUID = -2190121338982417134L;
    protected int position = -1;
    private boolean temporary = false;
    private String testKey;
    private String type;

    public e() {
        setBaseCardType(C0238a.C0040a.GUIDE);
    }

    public void bindView(int i, View view, Context context, g gVar) {
        this.position = i;
    }

    public abstract int getLayoutId();

    public String getTestKey() {
        return this.testKey;
    }

    public String getType() {
        return this.type;
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void onClick(View view) {
    }

    public void setTemporary(boolean z) {
        this.temporary = z;
    }

    public void setTestKey(String str) {
        this.testKey = str;
    }

    public void setType(String str) {
        this.type = str;
    }
}
