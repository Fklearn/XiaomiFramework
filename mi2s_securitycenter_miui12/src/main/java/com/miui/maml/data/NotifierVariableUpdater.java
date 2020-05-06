package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import com.miui.maml.NotifierManager;

public abstract class NotifierVariableUpdater extends VariableUpdater implements NotifierManager.OnNotifyListener {
    protected NotifierManager mNotifierManager = NotifierManager.getInstance(getContext().mContext);
    private String mType;

    public NotifierVariableUpdater(VariableUpdaterManager variableUpdaterManager, String str) {
        super(variableUpdaterManager);
        this.mType = str;
    }

    public void finish() {
        this.mNotifierManager.releaseNotifier(this.mType, this);
    }

    public void init() {
        this.mNotifierManager.acquireNotifier(this.mType, this);
    }

    public abstract void onNotify(Context context, Intent intent, Object obj);

    public void pause() {
        this.mNotifierManager.pause(this.mType, this);
    }

    public void resume() {
        this.mNotifierManager.resume(this.mType, this);
    }
}
