package com.miui.maml.data;

import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;

public class VariableUpdater {
    private VariableUpdaterManager mVariableUpdaterManager;

    public VariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        this.mVariableUpdaterManager = variableUpdaterManager;
    }

    public void finish() {
    }

    /* access modifiers changed from: protected */
    public final ScreenContext getContext() {
        return getRoot().getContext();
    }

    /* access modifiers changed from: protected */
    public final ScreenElementRoot getRoot() {
        return this.mVariableUpdaterManager.getRoot();
    }

    public void init() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void tick(long j) {
    }
}
