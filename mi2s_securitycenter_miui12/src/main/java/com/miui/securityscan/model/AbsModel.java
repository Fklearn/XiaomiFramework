package com.miui.securityscan.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.miui.securitycenter.Application;

public abstract class AbsModel {
    public static final int REQUEST_CODE_AUTO_ITEM = 101;
    public static final int REQUEST_CODE_GARBAGE_CLEAN = 103;
    public static final int REQUEST_CODE_MANUAL_ITEM = 100;
    private boolean checked = false;
    private boolean delayOptimize = false;
    private Handler firstAidEventHandler;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isFixed = false;
    private String itemKey;
    private AbsModelDisplayListener onAbsModelDisplayListener;
    private State safeState = State.SAFE;
    private boolean scanHide = false;
    private Integer score;
    private String trackIgnoreStr;
    private String trackStr;

    public interface AbsModelDisplayListener {
        void onAbsModelDisplay();
    }

    public enum State {
        DANGER,
        DANGER_MINOR,
        SAFE
    }

    public AbsModel(String str, Integer num) {
        this.itemKey = str;
        this.score = num;
    }

    public String getButtonTitle() {
        return "";
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return Application.d();
    }

    public abstract String getDesc();

    public Handler getFirstAidEventHandler() {
        return this.firstAidEventHandler;
    }

    public abstract int getIndex();

    public String getItemKey() {
        return this.itemKey;
    }

    public AbsModelDisplayListener getOnAbsModelDisplayListener() {
        return this.onAbsModelDisplayListener;
    }

    public int getScore() {
        Integer num = this.score;
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public abstract String getSummary();

    public abstract String getTitle();

    public String getTrackIgnoreStr() {
        return this.trackIgnoreStr;
    }

    public String getTrackStr() {
        return this.trackStr;
    }

    public void ignore() {
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isDelayOptimized() {
        return this.delayOptimize;
    }

    public boolean isFixed() {
        return this.isFixed;
    }

    public State isSafe() {
        return this.safeState;
    }

    public boolean isScanHide() {
        return this.scanHide;
    }

    public abstract void optimize(Context context);

    /* access modifiers changed from: protected */
    public void runOnUiThread(Runnable runnable) {
        this.handler.post(runnable);
    }

    public abstract void scan();

    public void setChecked(boolean z) {
        this.checked = z;
    }

    /* access modifiers changed from: protected */
    public void setDelayOptimized(boolean z) {
        this.delayOptimize = z;
    }

    public void setFirstAidEventHandler(Handler handler2) {
        this.firstAidEventHandler = handler2;
    }

    public void setFixed(boolean z) {
        this.isFixed = z;
    }

    public void setOnAbsModelDisplayListener(AbsModelDisplayListener absModelDisplayListener) {
        this.onAbsModelDisplayListener = absModelDisplayListener;
    }

    public void setSafe(State state) {
        this.safeState = state;
    }

    /* access modifiers changed from: protected */
    public void setScanHide(boolean z) {
        this.scanHide = z;
    }

    public void setTrackIgnoreStr(String str) {
        this.trackIgnoreStr = str;
    }

    /* access modifiers changed from: protected */
    public void setTrackStr(String str) {
        this.trackStr = str;
    }
}
