package com.miui.maml.data;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import java.util.ArrayList;
import java.util.Iterator;

public class VariableUpdaterManager {
    public static final String USE_TAG_NONE = "none";
    private ScreenElementRoot mRoot;
    private ArrayList<VariableUpdater> mUpdaters = new ArrayList<>();

    public VariableUpdaterManager(ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
    }

    public void add(VariableUpdater variableUpdater) {
        this.mUpdaters.add(variableUpdater);
    }

    public void addFromTag(String str) {
        VariableUpdater batteryVariableUpdater;
        if (!TextUtils.isEmpty(str) && !"none".equalsIgnoreCase(str)) {
            for (String trim : str.split(",")) {
                String trim2 = trim.trim();
                String str2 = null;
                int indexOf = trim2.indexOf(46);
                if (indexOf != -1) {
                    String substring = trim2.substring(0, indexOf);
                    str2 = trim2.substring(indexOf + 1);
                    trim2 = substring;
                }
                if (trim2.equals("DateTime")) {
                    batteryVariableUpdater = new DateTimeVariableUpdater(this, str2);
                } else if (trim2.equals(BatteryVariableUpdater.USE_TAG)) {
                    batteryVariableUpdater = new BatteryVariableUpdater(this);
                }
                add(batteryVariableUpdater);
            }
        }
    }

    public void finish() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    public void init() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    public void pause() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void remove(VariableUpdater variableUpdater) {
        this.mUpdaters.remove(variableUpdater);
    }

    public void resume() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }

    public void tick(long j) {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().tick(j);
        }
    }
}
